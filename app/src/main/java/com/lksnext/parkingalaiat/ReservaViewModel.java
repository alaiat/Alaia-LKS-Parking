package com.lksnext.parkingalaiat;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.lksnext.parkingalaiat.domain.Reserva;

import java.util.List;

public class ReservaViewModel extends AndroidViewModel {
    private ReservaRepository repository;
    private LiveData<List<Reserva>> allReservas;
    private LiveData<List<Reserva>> reservasActivas;
    private LiveData<List<Reserva>> reservasInactivas;
    public ReservaViewModel(@NonNull Application application) {
        super(application);
        repository=new ReservaRepository(application);
        allReservas=repository.getAllreservas();
    }
    public void insert(Reserva reserva){
        repository.insert(reserva);
    }
    public void update(Reserva reserva){
        repository.update(reserva);
    }
    public void delete(Reserva reserva){
        repository.delete(reserva);
    }
    public LiveData<List<Reserva>> getAllReservas(){
        return allReservas;
    }
    public LiveData<List<Reserva>> getReservasActivas(){
        return reservasActivas;
    }
    public LiveData<List<Reserva>> getReservasInactivas(){
        return reservasInactivas;
    }
}
