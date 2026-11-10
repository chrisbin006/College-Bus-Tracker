package com.trackmybus.service;

import com.trackmybus.repo.UserRepository;
import com.trackmybus.user.*;
import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/** Registration, login and session tokens. Passwords are salted and hashed with PBKDF2. */
public class AuthService {
    private final UserRepository repo;
    private final String driverCode;
    private final Map<String, String> sessions = new ConcurrentHashMap<>();
    private final SecureRandom random = new SecureRandom();

    public AuthService(UserRepository repo, String driverCode) { this.repo = repo; this.driverCode = driverCode; }

    private String hash(String password, String salt) {
        try {
            byte[] h = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                    .generateSecret(new PBEKeySpec(password.toCharArray(), Base64.getDecoder().decode(salt), 65_000, 256)).getEncoded();
            return Base64.getEncoder().encodeToString(h);
        } catch (Exception e) { throw new IllegalStateException(e); }
    }
    private String newSalt() { byte[] b = new byte[16]; random.nextBytes(b); return Base64.getEncoder().encodeToString(b); }
    private String newToken() { byte[] b = new byte[24]; random.nextBytes(b); return HexFormat.of().formatHex(b); }

    public synchronized String register(String name, String username, String password, String role, String busNo,
                                        String code, int defaultPickup, int college) throws AuthException {
        username = username == null ? "" : username.trim().toLowerCase();
        if (!username.matches("[a-z0-9_.]{3,20}")) throw new AuthException("Username: 3 to 20 letters, numbers, dots or underscores.", 400);
        if (password == null || password.length() < 6) throw new AuthException("Password needs at least 6 characters.", 400);
        if (name == null || name.isBlank()) throw new AuthException("Enter your name.", 400);
        if (repo.find(username).isPresent()) throw new AuthException("Username already taken.", 409);
        String salt = newSalt(), h = hash(password, salt);
        User u;
        if ("driver".equals(role)) {
            if (!driverCode.equals(code)) throw new AuthException("Wrong driver code.", 403);
            u = new Driver(username, name.trim(), salt, h, busNo);
        } else u = new Student(username, name.trim(), salt, h, defaultPickup, college);
        repo.save(u);
        return open(username);
    }

    public String login(String username, String password) throws AuthException {
        User u = repo.find(username == null ? "" : username.trim().toLowerCase()).orElse(null);
        if (u == null || password == null || !u.getHash().equals(hash(password, u.getSalt())))
            throw new AuthException("Wrong username or password.", 401);
        return open(u.getUsername());
    }
    private String open(String username) { String t = newToken(); sessions.put(t, username); return t; }
    public void logout(String token) { if (token != null) sessions.remove(token); }

    public User require(String token) throws AuthException {
        String name = token == null ? null : sessions.get(token);
        return repo.find(name == null ? "" : name).orElseThrow(() -> new AuthException("Please log in again.", 401));
    }
    public void save(User u) { repo.save(u); }
}
