package com.lksnext.parkingalaiat.domain;

import com.google.firebase.database.PropertyName;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String name;
    private String email;
    private String phoneNumber;
    @PropertyName("reservas")
    private List<Reserva> reservas;



    public User(String nombre, String correoElectronico, String phoneNum) {
        this.name = nombre;
        this.email = correoElectronico;
        this.phoneNumber=phoneNum;
        reservas=new ArrayList<>();
    }

    // Getters y setters

    public List<Reserva> getReservas() {
        return reservas;
    }

    public void setReservas(List<Reserva> reservas) {
        this.reservas = reservas;
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
