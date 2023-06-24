package com.lksnext.parkingalaiat.domain;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "spot_table")
public class Spot {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private SpotType type;
    private boolean available;
    //private ReservationManager reservationManager;

    public Spot( SpotType type) {
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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