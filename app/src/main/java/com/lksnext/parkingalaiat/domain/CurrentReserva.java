package com.lksnext.parkingalaiat.domain;

public class CurrentReserva {

    private static CurrentReserva instance;
    private Reserva current;
    private String id;

    private CurrentReserva() {
        // Private constructor to prevent instantiation from outside
    }

    public static synchronized CurrentReserva getInstance() {
        if (instance == null) {
            instance = new CurrentReserva();
        }
        return instance;
    }

    public Reserva getCurrent() {
        return current;
    }

    public void setCurrent(Reserva reserva) {
        current = reserva;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

