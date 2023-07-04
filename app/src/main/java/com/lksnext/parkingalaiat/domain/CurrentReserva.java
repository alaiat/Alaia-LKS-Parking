package com.lksnext.parkingalaiat.domain;

public class CurrentReserva {

    private static CurrentReserva instance;
    private ReservaOld currentReserva;

    private CurrentReserva() {
        // Private constructor to prevent instantiation from outside
    }

    public static synchronized CurrentReserva getInstance() {
        if (instance == null) {
            instance = new CurrentReserva();
        }
        return instance;
    }

    public ReservaOld getCurrentReserva() {
        return currentReserva;
    }

    public void setCurrentReserva(ReservaOld reserva) {
        currentReserva = reserva;
    }
}

