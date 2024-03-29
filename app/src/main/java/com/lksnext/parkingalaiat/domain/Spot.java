package com.lksnext.parkingalaiat.domain;

public class Spot {


    private int number;
    private String type;
    private boolean available;


    public Spot(String type, int number) {
        this.type = type;
        this.available = true;
        this.number = number;

    }


    public enum SpotType {
        CAR,
        MOTORCYCLE,
        HANDICAPPED,
        ELECTRIC
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }


    @Override
    public String toString() {
        return type;
    }
}