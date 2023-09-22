package com.lksnext.parkingalaiat.domain;

public class CurrentBooking {

    private static CurrentBooking instance;
    private Booking current;
    private String id;

    private CurrentBooking() {
        // Private constructor to prevent instantiation from outside
    }

    public static synchronized CurrentBooking getInstance() {
        if (instance == null) {
            instance = new CurrentBooking();
        }
        return instance;
    }

    public Booking getCurrent() {
        return current;
    }

    public void setCurrent(Booking booking) {
        current = booking;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

