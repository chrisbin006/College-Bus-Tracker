package com.trackmybus.model;

/** Abstract vehicle: shared data, with behaviour that subclasses must define (abstraction). */
public abstract class Vehicle {
    private final String number;
    protected final Route route;

    protected Vehicle(String number, Route route) { this.number = number; this.route = route; }

    public String getNumber() { return number; }
    public Route getRoute() { return route; }
    public double durationMinutes() { return route.totalMinutes(); }

    public abstract double speedKmh();
    public abstract String type();
}
