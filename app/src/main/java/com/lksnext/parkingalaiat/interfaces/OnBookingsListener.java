package com.lksnext.parkingalaiat.interfaces;

import com.lksnext.parkingalaiat.domain.Booking;

import java.util.List;

public interface OnBookingsListener {
    void onBookingsLoaded(List<Booking> bookings);
}
