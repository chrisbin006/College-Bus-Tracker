package com.trackmybus.model;

import java.util.*;

/** An ordered list of stops with cumulative travel minutes at a given speed. */
public class Route {
    private final List<Stop> stops;
    private final double[] cum;

    public Route(List<Stop> stops, double speedKmh) {
        this.stops = List.copyOf(stops);
        cum = new double[stops.size()];
        for (int k = 1; k < cum.length; k++) {
            Stop a = stops.get(k - 1), b = stops.get(k);
            cum[k] = cum[k - 1] + Geo.roadKm(a.getLat(), a.getLng(), b.getLat(), b.getLng()) / speedKmh * 60;
        }
    }
    public List<Stop> getStops() { return stops; }
    public double totalMinutes() { return cum[cum.length - 1]; }
    public int indexOf(int stopId) {
        for (int i = 0; i < stops.size(); i++) if (stops.get(i).getId() == stopId) return i;
        return -1;
    }
    /** True when the route visits a before b. */
    public boolean serves(int stopA, int stopB) {
        int i = indexOf(stopA), j = indexOf(stopB);
        return i >= 0 && j > i;
    }
    /** Position after travelling the given number of minutes. */
    public Position at(double minutes) {
        double m = Math.max(0, Math.min(minutes, totalMinutes()));
        int k = 1;
        while (k < cum.length - 1 && cum[k] < m) k++;
        double span = cum[k] - cum[k - 1], f = (m - cum[k - 1]) / (span == 0 ? 1 : span);
        Stop a = stops.get(k - 1), b = stops.get(k);
        return new Position(a.getLat() + (b.getLat() - a.getLat()) * f, a.getLng() + (b.getLng() - a.getLng()) * f);
    }
    /** Minutes of travel at which a GPS point is closest to this route. */
    public double progressOf(double lat, double lng) {
        double best = Double.MAX_VALUE, mm = 0;
        for (int k = 1; k < cum.length; k++) {
            Stop a = stops.get(k - 1), b = stops.get(k);
            double dx = b.getLng() - a.getLng(), dy = b.getLat() - a.getLat();
            double f = Math.max(0, Math.min(1, ((lng - a.getLng()) * dx + (lat - a.getLat()) * dy) / (dx * dx + dy * dy)));
            double d = Math.hypot(lng - (a.getLng() + dx * f), lat - (a.getLat() + dy * f));
            if (d < best) { best = d; mm = cum[k - 1] + f * (cum[k] - cum[k - 1]); }
        }
        return mm;
    }
}
