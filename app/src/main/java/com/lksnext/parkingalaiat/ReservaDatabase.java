package com.lksnext.parkingalaiat;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.lksnext.parkingalaiat.domain.Reserva;

@Database(entities = {Reserva.class},version=1)
public abstract class ReservaDatabase  extends RoomDatabase {

    private static ReservaDatabase instance;

    public abstract FuncionalidadesConReservas funcionalidades();

    public static synchronized ReservaDatabase getInstance(Context context){
        if(instance==null){
            instance= Room.databaseBuilder(context.getApplicationContext(), ReservaDatabase.class,"reserva_database")
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build();
        }
        return instance;
    }
    private static RoomDatabase.Callback roomCallback=new RoomDatabase.Callback(){
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db){
            super.onCreate(db);
            new PopulateDbAsyncTask(instance).execute();
        }};
    private static class PopulateDbAsyncTask extends AsyncTask<Void,Void,Void> {
        private FuncionalidadesConReservas funcionalidades;

        private PopulateDbAsyncTask(ReservaDatabase db){
            funcionalidades=db.funcionalidades();
        }
        @Override
        protected Void doInBackground(Void... voids){
            funcionalidades.insert(new Reserva("Electric","08:00","13:00","23/07/2023","Activo"));
            funcionalidades.insert(new Reserva("Normal","14:00","19:00","23/07/2023","Activo"));
            funcionalidades.insert(new Reserva("Normal","12:00","14:00","21/07/2023","Activo"));
            funcionalidades.insert(new Reserva("Normal","08:00","11:00","21/07/2023","Activo"));
            funcionalidades.insert(new Reserva("Normal","08:00","09:00","21/07/2023","Activo"));
            funcionalidades.insert(new Reserva("Electric","08:00","13:00","20/07/2023","Activo"));
            funcionalidades.insert(new Reserva("Electric","08:00","13:00","20/06/2023","Inactivo"));
            funcionalidades.insert(new Reserva("Electric","08:00","13:00","20/06/2023","Inactivo"));
            funcionalidades.insert(new Reserva("Electric","08:00","13:00","20/06/2023","Inactivo"));
            return null;
        }
    }

}
