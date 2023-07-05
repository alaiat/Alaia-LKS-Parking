package com.lksnext.parkingalaiat.interfaces;

import com.lksnext.parkingalaiat.domain.CurrentParking;

import java.util.List;

public interface OnSpotsLoaderByTypeListener {
    void OnSpotsLoaderByTypeListener(List<CurrentParking.Spota> sortedList);
}
