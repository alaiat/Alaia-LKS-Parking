package com.lksnext.parkingalaiat.domain;

import com.lksnext.parkingalaiat.FirebaseManager;
import com.lksnext.parkingalaiat.interfaces.OnSpotIdsLoadedListener;
import com.lksnext.parkingalaiat.interfaces.OnSpotsLoaderByTypeListener;

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
    private void addSpotsById(){
        fm.getAllSpotsId(new OnSpotIdsLoadedListener() {
            @Override
            public void onSpotIdsLoaded(List<String> spotIds) {
                if (spotIds != null) {
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

        public String getNum() {
            return num;
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
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }


}

