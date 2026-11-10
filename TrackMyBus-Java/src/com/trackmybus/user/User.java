package com.trackmybus.user;

import java.util.LinkedHashMap;
import java.util.Map;

/** Abstract account. Subclasses define their role and extra data (polymorphism). */
public abstract class User {
    private final String username, name, salt, hash;

    protected User(String username, String name, String salt, String hash) {
        this.username = username; this.name = name; this.salt = salt; this.hash = hash;
    }
    public String getUsername() { return username; }
    public String getName() { return name; }
    public String getSalt() { return salt; }
    public String getHash() { return hash; }

    public abstract Role role();
    /** Role-specific fields, saved as two text columns. */
    public abstract String[] extras();
    public abstract Map<String, Object> toPublicMap();

    protected Map<String, Object> baseMap() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("username", username); m.put("name", name); m.put("role", role().name().toLowerCase());
        return m;
    }
}
