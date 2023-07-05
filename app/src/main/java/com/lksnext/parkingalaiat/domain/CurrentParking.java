package com.lksnext.parkingalaiat.domain;

import com.lksnext.parkingalaiat.FirebaseManager;
import com.lksnext.parkingalaiat.interfaces.OnReservationsListener;
import com.lksnext.parkingalaiat.interfaces.OnSpotIdsLoadedListener;
import com.lksnext.parkingalaiat.interfaces.OnSpotsLoaderByTypeListener;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
public class CurrentParking {
    private static CurrentParking instance;
    private List<String> spotListaId;
    private List<Spota> all=new ArrayList<>();
    private List<Spota> car=new ArrayList<>();
    private List<Spota> hand=new ArrayList<>();
    private List<Spota> elec=new ArrayList<>();
    private List<Spota> motor=new ArrayList<>();
    FirebaseManager fm;

    private CurrentParking() {
        fm= FirebaseManager.getInstance();
        spotListaId =new ArrayList<>();
        addSpotsById();
    }
    public void addSpotsById(){
        fm.getAllSpotsId(new OnSpotIdsLoadedListener() {
            @Override
            public void onSpotIdsLoaded(List<String> spotIds) {
                if (spotIds != null) {
                    spotListaId.clear();
                    car.clear();
                    motor.clear();
                    hand.clear();
                    elec.clear();
                    spotListaId=spotIds;
                    addSpotToCorrepondingList();
                } else {
                    // Handle the failure case
                    // ...
                }
            }
        });
    }

    private void addSpotToCorrepondingList() {
       fm.sortList(spotListaId, new OnSpotsLoaderByTypeListener() {
            @Override
            public void OnSpotsLoaderByTypeListener(List<Spota> sortedList) {
                all=sortedList;
                for(Spota s:all){
                    switch (s.type) {
                        case "CAR":
                            car.add(s);
                            break;
                        case "HANDICAPPED":
                            hand.add(s);
                            break;
                        case "MOTORCYCLE":
                            motor.add(s);
                            break;
                        case "ELECTRIC":
                            elec.add(s);
                            break;
                        default:
                            break;
                    }
                }
            }
        });

    }



    public String getIdByNum(String num){

        for (Spota spota : all) {
            if (spota.getNum().equals(num)) {
                return spota.getId();
            }
        }
        return null; // Return null if no matching num is found

    }
    public String getNumById(String id){

        for (Spota spota : all) {
            if (spota.getId().equals(id)) {
                return spota.getNum();
            }
        }
        return null; // Return null if no matching num is found

    }
    public String getTypeById(String id){

        for (Spota spota : all) {
            if (spota.getId().equals(id)) {
                return spota.getType();
            }
        }
        return null; // Return null if no matching num is found

    }



    public  static synchronized CurrentParking getInstance() {

                if (instance == null) {
                    instance = new CurrentParking();
                }


        return instance;
    }


    public List<String> getSpotListaId() {
        return spotListaId;
    }

    public void setSpotListaId(List<String> spotListaId) {
        this.spotListaId = spotListaId;
    }

    public List<Spota> getAll() {
        return all;
    }

    public List<Spota> getCar() {
        return car;
    }

    public List<Spota> getHand() {
        return hand;
    }

    public List<Spota> getElec() {
        return elec;
    }

    public List<Spota> getMotor() {
        return motor;
    }

    public static class Spota{
        private String num;
        private String id;
        private String type;
        private List<DayHours> listDayTime;
        FirebaseManager fm=FirebaseManager.getInstance();

        public String getNum() {
            return num;
        }

        public List<DayHours> getListDayTime() {
            return listDayTime;
        }

        public void setListDayTime(List<DayHours> listDayTime) {
            this.listDayTime = listDayTime;
        }

        public void setNum(String num) {
            this.num = num;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public Spota(String num, String id, String type) {
            this.num = num;
            this.id = id;
            this.type=type;
            listDayTime=new ArrayList<>();
            findReservations(id);
        }

        private void findReservations(String id) {
            fm.getReservationsForSpot(id, new OnReservationsListener() {
                @Override
                public void onReservationsLoaded(List<Reserva> reservations) {
                    // Handle the reservations list
                    // ...
                    addListtoList(reservations);
                    System.out.println(listDayTime);

                }


            });

        }
        public void addListtoList(List<Reserva> reservations) {

            for(Reserva r:reservations){
                Boolean found=false;
                DayHours dh=null;
                for(DayHours d:listDayTime){
                    if(d.getDate().equals(r.getDate())){
                        found=true;
                        dh=d;
                    }
                }
                if(!found){
                    dh=new DayHours(r.getDate());
                    found=false;
                }

                dh.addStartHour(r.getStartTime());
                dh.addEndHour(r.getEndTime());
                dh.addAndCalculateNewTime(r.getStartTime(),r.getEndTime());
                System.out.println(dh.getStartHours());
                listDayTime.add(dh);
            }
        }
        public void addReservaToList(Reserva r){
            DayHours d=null;
            boolean found=false;
            for(DayHours dh:listDayTime){
                if(dh.getDate().equals(r.getDate())){
                    found=true;
                    d=dh;
                }
            }
            if(!found){
                d= new DayHours(r.getDate());
                listDayTime.add(d);

            }
            d.addStartHour(r.getStartTime());
            d.addEndHour(r.getEndTime());
            d.addAndCalculateNewTime(r.getStartTime(),r.getEndTime());
        }
        public DayHours getDayHoursByDay(String date){
            for(DayHours dh:listDayTime){
                if(dh.getDate().equals(date)){
                    return dh;
                }
            }
            return null;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        @Override
        public String toString() {
            return "Spota{" +
                    "num='" + num + '\'' +
                    ", id='" + id + '\'' +
                    ", type='" + type + '\'' +
                    ", listDayTime=" + listDayTime +
                    '}';
        }
    }


}

