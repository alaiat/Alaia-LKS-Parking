package com.lksnext.parkingalaiat.domain;

import java.util.ArrayList;
import java.util.List;

public class ParkingLot {
    private String name;
    private List<Spot> spotList;
    private List<String> users;
    public ParkingLot(String name){
        this.name=name;
        spotList= new ArrayList<>();
        users=new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Spot> getSpotList() {
        return spotList;
    }

    public void addSpot(Spot spot){
        spotList.add(spot);
    }

    public void setSpotList(List<Spot> spotList) {
        this.spotList = spotList;
    }

    public List<String> getUsers() {
        return users;
    }

    public void setUsers(List<String> users) {
        this.users = users;
    }

    @Override
    public String toString() {
        return "ParkingLot{" +
                "name='" + name + '\'' +
                ", spotList=" + spotList +
                ", users=" + users +
                '}';
    }
}