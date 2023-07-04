package com.lksnext.parkingalaiat.domain;

public class CurrentReserva {

    private static CurrentReserva instance;
    private Reserva currentReserva;

    private CurrentReserva() {
        // Private constructor to prevent instantiation from outside
    }

    public static synchronized CurrentReserva getInstance() {
        if (instance == null) {
            instance = new CurrentReserva();
        }
        return instance;
    }

    public Reserva getCurrentReserva() {
        return currentReserva;
    }

    public void setCurrentReserva(Reserva reserva) {
        currentReserva = reserva;
    }
}

