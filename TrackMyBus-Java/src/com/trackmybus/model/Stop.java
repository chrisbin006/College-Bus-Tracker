package com.trackmybus.model;

/** A bus stop: immutable value object (encapsulation). */
public final class Stop {
    private final int id; private final String name; private final double lat, lng; private final String district;
    public Stop(int id, String name, double lat, double lng, String district) {
        this.id = id; this.name = name; this.lat = lat; this.lng = lng; this.district = district;
    }
    public int getId() { return id; }
    public String getName() { return name; }
    public double getLat() { return lat; }
    public double getLng() { return lng; }
    public String getDistrict() { return district; }
    @Override public String toString() { return name; }
}
