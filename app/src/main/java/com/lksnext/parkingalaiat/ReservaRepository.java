package com.lksnext.parkingalaiat;

import androidx.lifecycle.LiveData;
import android.app.Application;
import android.os.AsyncTask;

import com.lksnext.parkingalaiat.domain.Reserva;

import java.util.List;

public class ReservaRepository {
    private FuncionalidadesConReservas funcionalidades;
    private LiveData<List<Reserva>> allReservas;

    public ReservaRepository(Application aplication){
        ReservaDatabase db=ReservaDatabase.getInstance(aplication);
        funcionalidades=db.funcionalidades();
        allReservas= funcionalidades.getAllReservas();
    }
    public void insert(Reserva reserva){
        new InsertReservaAsyncTask(funcionalidades).execute(reserva);
    }
    public void update(Reserva reserva){
        new UpdateReservaAsyncTask(funcionalidades).execute(reserva);

    }

    public void delete(Reserva reserva){
        new DeleteReservaAsyncTask(funcionalidades).execute(reserva);


    }
    public LiveData<List<Reserva>> getAllreservas() {
        return allReservas;
    }
    private static class InsertReservaAsyncTask extends AsyncTask<Reserva,Void,Void> {

        private FuncionalidadesConReservas funcionalidades;
        private InsertReservaAsyncTask(FuncionalidadesConReservas funcionalidades){
            this.funcionalidades=funcionalidades;
        }

        @Override
        protected Void doInBackground(Reserva... reservas) {
            funcionalidades.insert(reservas[0]);
            return null;
        }
    }
    private static class UpdateReservaAsyncTask extends AsyncTask<Reserva,Void,Void> {

        private FuncionalidadesConReservas funcionalidades;
        private UpdateReservaAsyncTask(FuncionalidadesConReservas funcionalidades){
            this.funcionalidades=funcionalidades;
        }

        @Override
        protected Void doInBackground(Reserva... reservas) {
            funcionalidades.update(reservas[0]);
            return null;
        }
    }
    private static class DeleteReservaAsyncTask extends AsyncTask<Reserva,Void,Void> {

        private FuncionalidadesConReservas funcionalidades;
        private DeleteReservaAsyncTask(FuncionalidadesConReservas funcionalidades){
            this.funcionalidades=funcionalidades;
        }

        @Override
        protected Void doInBackground(Reserva... reservas) {
            funcionalidades.delete(reservas[0]);
            return null;
        }
    }

}
