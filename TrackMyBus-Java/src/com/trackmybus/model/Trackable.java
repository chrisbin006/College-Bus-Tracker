package com.trackmybus.model;

import java.time.LocalTime;

/** Anything whose real-time status can be asked for (interface). */
public interface Trackable {
    BusStatus status(LocalTime now, LiveFix live);
}
