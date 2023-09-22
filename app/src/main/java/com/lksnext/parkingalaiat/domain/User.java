package com.lksnext.parkingalaiat.domain;

import com.google.firebase.database.PropertyName;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String name;
    private String email;
    private String phoneNumber;
    private List<Booking> bookings;



    public User(String name, String email, String phone) {
        this.name = name;
        this.email = email;
        this.phoneNumber=phone;
        bookings =new ArrayList<>();
    }


    public List<Booking> getReservas() {
        return bookings;
    }

    public void setReservas(List<Booking> bookings) {
        this.bookings = bookings;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber){
        this.phoneNumber=phoneNumber;
    }


}
