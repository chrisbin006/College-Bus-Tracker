package com.trackmybus.user;

import java.util.Map;

/** A student remembers a pickup stop and a final stop. */
public class Student extends User {
    private int pickup, finalStop;

    public Student(String username, String name, String salt, String hash, int pickup, int finalStop) {
        super(username, name, salt, hash);
        this.pickup = pickup; this.finalStop = finalStop;
    }
    public void setRoute(int pickup, int finalStop) { this.pickup = pickup; this.finalStop = finalStop; }
    @Override public Role role() { return Role.STUDENT; }
    @Override public String[] extras() { return new String[]{String.valueOf(pickup), String.valueOf(finalStop)}; }
    @Override public Map<String, Object> toPublicMap() {
        Map<String, Object> m = baseMap(); m.put("pickup", pickup); m.put("final", finalStop); return m;
    }
}
