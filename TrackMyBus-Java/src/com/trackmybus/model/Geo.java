package com.trackmybus.model;

/** Distance helper (haversine formula with a 1.3 road factor). */
public final class Geo {
    private Geo() {}
    public static double roadKm(double la1, double lg1, double la2, double lg2) {
        double r = Math.PI / 180;
        double x = Math.pow(Math.sin((la2 - la1) * r / 2), 2)
                 + Math.cos(la1 * r) * Math.cos(la2 * r) * Math.pow(Math.sin((lg2 - lg1) * r / 2), 2);
        return 12742 * Math.asin(Math.sqrt(x)) * 1.3;
    }
}
