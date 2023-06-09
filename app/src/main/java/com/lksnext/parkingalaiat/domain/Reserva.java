package com.lksnext.parkingalaiat.domain;

import java.util.Date;

public class Reserva {

    private int id;
    private Spot spot;
    private Vehicle vehicle;
    private Date startTime;
    private Date endTime;

    public Reserva(int id, Spot spot, Vehicle vehicle, Date startTime, Date endTime) {
        this.id = id;
        this.spot = spot;
        this.vehicle = vehicle;
        this.startTime = startTime;
        this.endTime = endTime;
    }
    public boolean isOverlapping(Reserva other) {
        //return startTime.before(other.getEndTime()) && other.getStartTime().before(endTime);
        return false;
    }
}
