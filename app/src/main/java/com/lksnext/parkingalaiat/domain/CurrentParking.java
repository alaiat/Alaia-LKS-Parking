package com.lksnext.parkingalaiat.domain;

import com.lksnext.parkingalaiat.FirebaseManager;
import com.lksnext.parkingalaiat.interfaces.OnBookingsListener;
import com.lksnext.parkingalaiat.interfaces.OnSpotsIdLoadedListener;
import com.lksnext.parkingalaiat.interfaces.OnSpotsLoaderByTypeListener;

import java.util.ArrayList;
import java.util.List;
public class CurrentParking {
    private static CurrentParking instance;
    private List<String> spotListaId;
    private List<Area> all=new ArrayList<>();
    private List<Area> car=new ArrayList<>();
    private List<Area> hand=new ArrayList<>();
    private List<Area> elec=new ArrayList<>();
    private List<Area> motor=new ArrayList<>();
    FirebaseManager fm;

    private CurrentParking() {
        fm= FirebaseManager.getInstance();
        spotListaId =new ArrayList<>();
        addSpotsById();
    }
    public void addSpotsById(){
        fm.getAllSpotsId(new OnSpotsIdLoadedListener() {
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
            public void OnSpotsLoaderByTypeListener(List<Area> sortedList) {
                all=sortedList;
                for(Area s:all){
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

        for (Area area : all) {
            if (area.getNumber().equals(num)) {
                return area.getId();
            }
        }
        return null; // Return null if no matching num is found

    }
    public String getNumById(String id){

        for (Area area : all) {
            if (area.getId().equals(id)) {
                return area.getNumber();
            }
        }
        return null; // Return null if no matching num is found

    }
    public String getTypeById(String id){

        for (Area area : all) {
            if (area.getId().equals(id)) {
                return area.getType();
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

    public List<Area> getAll() {
        return all;
    }

    public List<Area> getCar() {
        return car;
    }

    public List<Area> getHand() {
        return hand;
    }

    public List<Area> getElec() {
        return elec;
    }

    public List<Area> getMotor() {
        return motor;
    }

    public static class Area {
        private String number;
        private String id;
        private String type;
        private List<DayHours> listDayTime;
        FirebaseManager fm=FirebaseManager.getInstance();

        public Area(String number, String id, String type) {
            this.number = number;
            this.id = id;
            this.type=type;
            listDayTime=new ArrayList<>();
            findBookings(id);
        }

        public String getNumber() {
            return number;
        }

        public List<DayHours> getListDayTime() {
            return listDayTime;
        }

        public void setListDayTime(List<DayHours> listDayTime) {
            this.listDayTime = listDayTime;
        }

        public void setNumber(String number) {
            this.number = number;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }



        private void findBookings(String id) {
            fm.getBookingsForSpot(id, new OnBookingsListener() {
                @Override
                public void onBookingsLoaded(List<Booking> bookings) {
                    // Handle the reservations list
                    // ...
                    addListToList(bookings);
                    System.out.println(listDayTime);

                }


            });

        }
        public void addListToList(List<Booking> bookings) {

            for(Booking b:bookings){
                Boolean found=false;
                DayHours dh=null;
                for(DayHours d:listDayTime){
                    if(d.getDate().equals(b.getDate())){
                        found=true;
                        dh=d;
                    }
                }
                if(!found){
                    dh=new DayHours(b.getDate());
                    found=false;
                }

                dh.addStartHour(b.getStartTime());
                dh.addEndHour(b.getEndTime());
                dh.addAndCalculateNewTime(b.getStartTime(),b.getEndTime());
                System.out.println(dh.getStartHours());
                listDayTime.add(dh);
            }
        }

        public DayHours getDayHoursByDate(String date){
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
            return "Area{" +
                    "num='" + number + '\'' +
                    ", id='" + id + '\'' +
                    ", type='" + type + '\'' +
                    ", listDayTime=" + listDayTime +
                    '}';
        }
    }


}

