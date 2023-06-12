package com.lksnext.parkingalaiat.domain;

import java.util.Date;

public class Reserva {

    private User user;
    private Spot spot;
    private Date startTime;
    private Date endTime;

    public Reserva(User user, Spot spot, Date startTime, Date endTime) {
        this.user = user;
        this.spot = spot;
        this.startTime = startTime;
        this.endTime = endTime;
    }


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Spot getSpot() {
        return spot;
    }

    public void setSpot(Spot spot) {
        this.spot = spot;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }


    public boolean isOverlapping(Reserva other) {
        //return startTime.before(other.getEndTime()) && other.getStartTime().before(endTime);
        return false;
    }
}
