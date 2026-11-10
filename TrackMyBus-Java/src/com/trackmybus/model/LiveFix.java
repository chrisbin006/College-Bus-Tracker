package com.trackmybus.model;

/** A GPS fix reported by a driver's phone. */
public record LiveFix(String busNo, double lat, double lng, long timeMillis) {
    public boolean isFresh() { return System.currentTimeMillis() - timeMillis < 45_000; }
}
