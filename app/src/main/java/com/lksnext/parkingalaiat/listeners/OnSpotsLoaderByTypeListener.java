package com.lksnext.parkingalaiat.listeners;

import com.lksnext.parkingalaiat.domain.CurrentParking;

import java.util.List;

public interface OnSpotsLoaderByTypeListener {
    void OnSpotsLoaderByTypeListener(List<CurrentParking.Area> sortedList);
}
