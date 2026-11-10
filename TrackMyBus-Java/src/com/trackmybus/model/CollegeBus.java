package com.trackmybus.model;

import java.time.LocalTime;

/**
 * A college bus (inheritance from Vehicle, implements Trackable).
 * Morning buses reach college at 08:45; evening buses leave college at 16:10 or 16:15.
 */
public class CollegeBus extends Vehicle implements Trackable {
    public static final double SPEED = 38;
    private final Trip trip;
    private final LocalTime departure;

    public CollegeBus(String number, Route route, Trip trip) {
        super(number, route);
        this.trip = trip;
        if (trip == Trip.MORNING) {
            departure = LocalTime.of(8, 45).minusMinutes(Math.round(route.totalMinutes()));
        } else {
            int n = Integer.parseInt(number.replaceAll("\\D", ""));
            departure = n % 2 == 1 ? LocalTime.of(16, 15) : LocalTime.of(16, 10);
        }
    }
    @Override public double speedKmh() { return SPEED; }
    @Override public String type() { return "College bus"; }
    public Trip getTrip() { return trip; }
    public LocalTime getDeparture() { return departure; }

    @Override
    public BusStatus status(LocalTime now, LiveFix live) {
        double nowMin = now.getHour() * 60 + now.getMinute() + now.getSecond() / 60.0;
        double sinceDep = nowMin - (departure.getHour() * 60 + departure.getMinute());
        double dur = durationMinutes();
        if (live != null && live.isFresh()) {
            double m = route.progressOf(live.lat(), live.lng());
            boolean inWindow = sinceDep >= 0 && sinceDep <= dur + 30;
            double delay = inWindow ? Math.max(0, Math.min(60, sinceDep - m)) : 0;
            return new BusStatus(getNumber(), m >= dur - 0.3 ? "ARRIVED" : "RUNNING", m, delay, true,
                    departure.toString(), new Position(live.lat(), live.lng()));
        }
        String state = sinceDep < 0 ? "WAITING" : sinceDep >= dur ? "ARRIVED" : "RUNNING";
        return new BusStatus(getNumber(), state, sinceDep, 0, false, departure.toString(), route.at(sinceDep));
    }
}
