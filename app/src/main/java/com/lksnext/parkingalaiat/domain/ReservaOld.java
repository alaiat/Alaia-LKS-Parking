package com.lksnext.parkingalaiat.domain;

import java.util.Date;

public class ReservaOld {

    private String spot;
    private String user;
    private String startTime;
    private String endTime;
    private String date;
    private String activo;

    public ReservaOld(String spot, String startTime, String endTime, String date,String user) {
        this.spot = spot;
        this.startTime = startTime;
        this.endTime = endTime;
        this.date=date;
        activo="";
        this.user=user;
    }


    public String getActivo() {
        return activo;
    }

    public void setActivo(String activo) {
        this.activo = activo;
    }

    public String getSpot() {
        return spot;
    }

    public void setSpot(String spot) {
        this.spot = spot;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isOverlapping(ReservaOld other) {
        //return startTime.before(other.getEndTime()) && other.getStartTime().before(endTime);
        return false;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "ReservaOld{" +
                ", spot=" + spot +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }

}
