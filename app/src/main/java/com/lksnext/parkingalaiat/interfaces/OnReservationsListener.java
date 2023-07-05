package com.lksnext.parkingalaiat.interfaces;

import com.lksnext.parkingalaiat.domain.Reserva;

import java.util.List;

public interface OnReservationsListener {
    void onReservationsLoaded(List<Reserva> reservations);
}
