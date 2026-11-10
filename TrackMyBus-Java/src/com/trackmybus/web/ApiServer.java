package com.trackmybus.web;

import com.sun.net.httpserver.*;
import com.trackmybus.model.*;
import com.trackmybus.service.*;
import com.trackmybus.user.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.Executors;

/** HTTP layer: static website plus a small JSON API. Business rules live in the service classes. */
public class ApiServer {
    /** One API endpoint (functional interface, used with lambdas). */
    @FunctionalInterface interface Endpoint { Object handle(Map<String, String> body, String token, String query) throws Exception; }

    private final AuthService auth;
    private final BusService buses;
    private final Path webDir;
    private final HttpServer server;
    private final Map<String, Endpoint> post = new HashMap<>(), get = new HashMap<>();

    public ApiServer(int port, Path webDir, AuthService auth, BusService buses) throws IOException {
        this.auth = auth; this.buses = buses; this.webDir = webDir;
        server = HttpServer.create(new InetSocketAddress(port), 0);
        server.setExecutor(Executors.newFixedThreadPool(16)); // many students polling at once
        routes();
        server.createContext("/", this::dispatch);
    }
    public void start() { server.start(); }

    private Map<String, Object> session(String token, User u) { return Map.of("token", token, "user", u.toPublicMap()); }

    private void routes() {
        get.put("/api/data", (b, t, q) -> {
            List<Object> stops = new ArrayList<>(), routes = new ArrayList<>();
            for (Stop s : buses.getStops()) stops.add(List.of(s.getName(), s.getLat(), s.getLng(), s.getDistrict()));
            for (String[] r : DataSet.routes()) routes.add(Map.of("no", r[0], "stops", Arrays.asList(r[1].split(",")), "off", Integer.parseInt(r[2])));
            return Map.of("stops", stops, "routes", routes);
        });
        get.put("/api/live", (b, t, q) -> buses.liveFixes().stream().map(f -> Map.of("no", f.busNo(), "la", f.lat(), "lg", f.lng(),
                "age", System.currentTimeMillis() - f.timeMillis())).toList());
        get.put("/api/status", (b, t, q) -> buses.statuses(q != null && q.contains("evening") ? Trip.EVENING : Trip.MORNING, LocalTime.now()));
        get.put("/api/me", (b, t, q) -> Map.of("user", auth.require(t).toPublicMap()));
        post.put("/api/register", (b, t, q) -> {
            String busNo = b.getOrDefault("busNo", "");
            if ("driver".equals(b.get("role")) && !buses.exists(busNo)) throw new AuthException("Choose a valid bus.", 400);
            String tok = auth.register(b.get("name"), b.get("username"), b.get("password"), b.get("role"), busNo,
                    b.get("code"), buses.stopByName("Palakkad").getId(), buses.collegeId());
            return session(tok, auth.require(tok));
        });
        post.put("/api/login", (b, t, q) -> { String tok = auth.login(b.get("username"), b.get("password")); return session(tok, auth.require(tok)); });
        post.put("/api/logout", (b, t, q) -> { auth.logout(t); return Map.of("ok", true); });
        post.put("/api/prefs", (b, t, q) -> {
            User u = auth.require(t);
            if (u instanceof Student s) { s.setRoute(Integer.parseInt(b.get("pickup")), Integer.parseInt(b.get("final"))); auth.save(s); }
            return Map.of("ok", true);
        });
        post.put("/api/driver/location", (b, t, q) -> {
            Driver d = driver(t, b.get("no"));
            buses.report(d.getBusNo(), Double.parseDouble(b.get("la")), Double.parseDouble(b.get("lg")));
            return Map.of("ok", true);
        });
        post.put("/api/driver/stop", (b, t, q) -> { buses.clear(driver(t, b.get("no")).getBusNo()); return Map.of("ok", true); });
    }

    /** Polymorphism in action: any User is accepted, but only a Driver for its own bus passes. */
    private Driver driver(String token, String busNo) throws AuthException {
        User u = auth.require(token);
        if (u instanceof Driver d && d.getBusNo().equals(busNo)) return d;
        throw new AuthException("Only the driver of " + busNo + " can share its location.", 403);
    }

    private void dispatch(HttpExchange ex) throws IOException {
        String path = ex.getRequestURI().getPath();
        try {
            if (path.startsWith("/api/")) { api(ex, path); return; }
            String name = path.equals("/") ? "index.html" : path.substring(1);
            byte[] data = null;
            Path f = webDir.resolve(name).normalize();
            if (f.startsWith(webDir) && Files.isRegularFile(f)) data = Files.readAllBytes(f);   // folder next to the program
            else if (!name.contains("..")) try (InputStream in = ApiServer.class.getResourceAsStream("/web/" + name)) {
                if (in != null) data = in.readAllBytes();                                        // packed inside the jar
            }
            if (data == null) { send(ex, 404, "text/plain", "Not found"); return; }
            send(ex, 200, name.endsWith(".html") ? "text/html; charset=utf-8" : "application/octet-stream", data);
        } catch (Exception e) { send(ex, 500, "application/json", Json.write(Map.of("error", "Server error"))); }
    }

    private void api(HttpExchange ex, String path) throws IOException {
        boolean isPost = ex.getRequestMethod().equals("POST");
        Endpoint e = (isPost ? post : get).get(path);
        if (e == null) { send(ex, 404, "application/json", Json.write(Map.of("error", "Unknown endpoint"))); return; }
        String h = ex.getRequestHeaders().getFirst("Authorization");
        String token = h != null && h.startsWith("Bearer ") ? h.substring(7) : null;
        Map<String, String> body = isPost ? Json.readFlat(new String(ex.getRequestBody().readAllBytes(), StandardCharsets.UTF_8)) : Map.of();
        try { send(ex, 200, "application/json", Json.write(e.handle(body, token, ex.getRequestURI().getQuery()))); }
        catch (AuthException a) { send(ex, a.getStatus(), "application/json", Json.write(Map.of("error", a.getMessage()))); }
        catch (Exception x) { send(ex, 400, "application/json", Json.write(Map.of("error", "Bad request"))); }
    }

    private void send(HttpExchange ex, int code, String type, String text) throws IOException { send(ex, code, type, text.getBytes(StandardCharsets.UTF_8)); }
    private void send(HttpExchange ex, int code, String type, byte[] data) throws IOException {
        ex.getResponseHeaders().set("Cache-Control", "no-store");
        ex.getResponseHeaders().set("Content-Type", type.startsWith("application/json") ? "application/json; charset=utf-8" : type);
        ex.sendResponseHeaders(code, data.length);
        try (OutputStream o = ex.getResponseBody()) { o.write(data); }
    }
}
