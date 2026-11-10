package com.trackmybus.model;

import java.util.LinkedHashMap;
import java.util.Map;

/** Snapshot of a bus: state, delay and position. */
public record BusStatus(String busNo, String state, double progressMin, double delayMin, boolean live,
                        String departure, Position position) {
    public Map<String, Object> toMap() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("no", busNo); m.put("state", state); m.put("progressMin", Math.round(progressMin * 10) / 10.0);
        m.put("delayMin", Math.round(delayMin)); m.put("live", live); m.put("departs", departure);
        m.put("lat", position.lat()); m.put("lng", position.lng());
        return m;
    }
}
