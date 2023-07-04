package com.lksnext.parkingalaiat.domain;



public class Reserva {



    private int id;
    public String type;
    public String startHour;
    public String endHour;
    public String date;
    public String status;
    public Integer spot;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getSpot() {
        return spot;
    }

    public void setSpot(Integer spot) {
        this.spot = spot;
    }

    public Reserva(String type, String startHour, String endHour, String date, String status, Integer spot) {
        this.type = type;
        this.startHour = startHour;
        this.endHour = endHour;
        this.date = date;
        this.status=status;
        this.spot=spot;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStartHour() {
        return startHour;
    }

    public void setStartHour(String startHour) {
        this.startHour = startHour;
    }

    public String getEndHour() {
        return endHour;
    }

    public void setEndHour(String endHour) {
        this.endHour = endHour;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }


}
