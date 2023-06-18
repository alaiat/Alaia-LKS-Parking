package com.lksnext.parkingalaiat.domain;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class User {
    private String userId;
    private String name;
    private String email;
    private String phoneNumber;


    private List<Reserva> reservas;


    public User(String id,String nombre, String correoElectronico, String phoneNum) {
        this.userId = id;
        this.name = nombre;
        this.email = correoElectronico;
        this.phoneNumber=phoneNum;
        //reservas=new ArrayList<Reserva>();
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

    /*public int obtenerDuracionTotalReservasEnDia(Date fecha) {
        int duracionTotal = 0;

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fecha);

        for (Reserva reserva : reservas) {
            Calendar reservaCalendar = Calendar.getInstance();
            reservaCalendar.setTime(reserva.getFechaInicio());

            if (calendar.get(Calendar.YEAR) == reservaCalendar.get(Calendar.YEAR)
                    && calendar.get(Calendar.MONTH) == reservaCalendar.get(Calendar.MONTH)
                    && calendar.get(Calendar.DAY_OF_MONTH) == reservaCalendar.get(Calendar.DAY_OF_MONTH)) {
                // La reserva se encuentra en el día especificado
                duracionTotal += calcularDuracionReservaEnHoras(reserva);
            }
        }

        return duracionTotal;
    }*/

   /* private int calcularDuracionReservaEnHoras(Reserva reserva) {
        long duracionEnMilisegundos = reserva.getFechaFin().getTime() - reserva.getFechaInicio().getTime();
        int duracionEnHoras = (int) (duracionEnMilisegundos / (1000 * 60 * 60));
        return duracionEnHoras;
    }*/
}
