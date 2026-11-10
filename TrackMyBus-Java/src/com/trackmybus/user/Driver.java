package com.trackmybus.user;

import java.util.Map;

/** A driver may share GPS only for the bus assigned to the account. */
public class Driver extends User {
    private final String busNo;

    public Driver(String username, String name, String salt, String hash, String busNo) {
        super(username, name, salt, hash);
        this.busNo = busNo;
    }
    public String getBusNo() { return busNo; }
    @Override public Role role() { return Role.DRIVER; }
    @Override public String[] extras() { return new String[]{busNo, ""}; }
    @Override public Map<String, Object> toPublicMap() {
        Map<String, Object> m = baseMap(); m.put("busNo", busNo); m.put("pickup", 0); m.put("final", 0); return m;
    }
}
