package com.lksnext.parkingalaiat.domain;

public class Spot {
    private int id;
    private SpotType type;
    private boolean available;
    //private ReservationManager reservationManager;

    public Spot(int id, SpotType type) {
        this.id = id;
        this.type = type;
        this.available = true;
        //this.reservationManager = new ReservationManager();
    }



    public enum SpotType {
        CAR,
        MOTORCYCLE,
        HANDICAPPED,
        ELECTRIC
    }
}