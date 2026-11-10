package com.trackmybus.service;

/** Custom checked exception for login and registration problems. */
public class AuthException extends Exception {
    private final int status;
    public AuthException(String message, int status) { super(message); this.status = status; }
    public int getStatus() { return status; }
}
