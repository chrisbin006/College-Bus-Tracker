package com.trackmybus.service;

import com.trackmybus.model.*;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/** Builds all buses from the data set and answers status questions. */
public class BusService {
    private final List<Stop> stops = DataSet.stops();
    private final Map<Trip, List<CollegeBus>> buses = new EnumMap<>(Trip.class);
    private final Map<String, LiveFix> live = new ConcurrentHashMap<>();

    public BusService() {
        for (Trip t : Trip.values()) buses.put(t, new ArrayList<>());
        for (String[] r : DataSet.routes()) {
            List<Stop> list = new ArrayList<>();
            for (String n : r[1].split(",")) list.add(stopByName(n));
            buses.get(Trip.MORNING).add(new CollegeBus(r[0], new Route(list, CollegeBus.SPEED), Trip.MORNING));
            List<Stop> back = new ArrayList<>(list); Collections.reverse(back);
            buses.get(Trip.EVENING).add(new CollegeBus(r[0], new Route(back, CollegeBus.SPEED), Trip.EVENING));
        }
    }
    public List<Stop> getStops() { return stops; }
    public Stop stopByName(String name) {
        return stops.stream().filter(s -> s.getName().equals(name)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown stop " + name));
    }
    public int collegeId() { return stopByName("Jyothi Engineering College").getId(); }
    public boolean exists(String busNo) { return buses.get(Trip.MORNING).stream().anyMatch(b -> b.getNumber().equals(busNo)); }

    public List<Map<String, Object>> statuses(Trip trip, LocalTime now) {
        List<Map<String, Object>> out = new ArrayList<>();
        for (CollegeBus b : buses.get(trip)) out.add(b.status(now, live.get(b.getNumber())).toMap());
        return out;
    }
    public void report(String busNo, double lat, double lng) { live.put(busNo, new LiveFix(busNo, lat, lng, System.currentTimeMillis())); }
    public void clear(String busNo) { live.remove(busNo); }
    public List<LiveFix> liveFixes() { return live.values().stream().filter(LiveFix::isFresh).toList(); }
}
