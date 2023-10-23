package com.lksnext.parkingalaiat.listeners;

import com.lksnext.parkingalaiat.domain.Booking;

import java.util.List;

public interface OnBookingsListener {
    void onBookingsLoaded(Exception exception, List<Booking> bookings);
}
