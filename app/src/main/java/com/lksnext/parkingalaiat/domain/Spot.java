package com.lksnext.parkingalaiat.domain;




public class Spot {


    private int number;
    private SpotType type;
    private boolean available;


    public Spot( SpotType type) {
        this.type = type;
        this.available = true;

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

    public SpotType getType() {
        return type;
    }

    public void setType(SpotType type) {
        this.type = type;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}