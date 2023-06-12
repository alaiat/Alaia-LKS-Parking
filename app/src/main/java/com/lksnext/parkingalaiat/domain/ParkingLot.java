package com.lksnext.parkingalaiat.domain;

import java.util.ArrayList;
import java.util.List;

public class ParkingLot {
    private String name;
    private String address;
    private List<Spot> spotList;

    private List<Reserva> reservaList;

    public ParkingLot(String name, String address){
        this.name=name;
        this.address=address;
        spotList= new ArrayList<>();
        reservaList=new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<Spot> getSpotList() {
        return spotList;
    }

    public void addSpot(Spot spot){
        spotList.add(spot);
    }
}