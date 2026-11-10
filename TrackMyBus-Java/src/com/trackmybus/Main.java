package com.trackmybus;

import com.trackmybus.repo.FileUserRepository;
import com.trackmybus.service.*;
import com.trackmybus.web.ApiServer;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;

/** Entry point: wires the objects together, starts the server and opens the browser. */
public class Main {
    public static void main(String[] args) throws Exception {
        int first = Integer.parseInt(System.getenv().getOrDefault("PORT", "8080"));
        String code = System.getenv().getOrDefault("DRIVER_CODE", "JYOTHI-DRIVER");
        AuthService auth = new AuthService(new FileUserRepository(Path.of("data", "users.tsv")), code);
        BusService buses = new BusService();
        Path web = Path.of("web").toAbsolutePath().normalize();
        for (int port = first; port < first + 10; port++) {      // if a port is busy, try the next one
            try {
                new ApiServer(port, web, auth, buses).start();
                String url = "http://localhost:" + port;
                System.out.println("TrackMyBus is running at " + url + "  (driver code: " + code + ")");
                System.out.println("Press Ctrl+C to stop.");
                try { if (Desktop.isDesktopSupported()) Desktop.getDesktop().browse(URI.create(url)); } catch (Throwable ignored) {}
                return;
            } catch (IOException busy) { System.out.println("Port " + port + " is busy, trying the next one..."); }
        }
        System.out.println("No free port found. Close other programs or set PORT.");
    }
}
