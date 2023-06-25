package com.lksnext.parkingalaiat;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.lksnext.parkingalaiat.domain.Reserva;

import java.util.List;

@Dao
public interface FuncionalidadesConReservas {
    @Insert
    void insert(Reserva reserva);
    @Update
    void update(Reserva reserva);
    @Delete
    void delete(Reserva reserva);

    @Query("SELECT * FROM reserva_table")
    LiveData<List<Reserva>> getAllReservas();
    @Query("SELECT * FROM reserva_table where status='Activo'")
    LiveData<List<Reserva>> getReservasActivas();
    @Query("SELECT * FROM reserva_table WHERE status='Inactivo'")
    LiveData<List<Reserva>> getReservasInactivas();
}
