package com.lksnext.parkingalaiat.domain;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

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

    private CurrentParking() {
        spotListaId =new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Get a reference to the "spots" collection
        CollectionReference spotsRef = db.collection("spots");

        // Query all documents in the "spots" collection
        spotsRef.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
                            // Get the spot ID from the document
                            String spotId = documentSnapshot.getId();
                            //System.out.println(spotId);

                            // Add the spot ID to the list
                            spotListaId.add(spotId);
                        }

                        // Print or use the spot IDs as needed
                        System.out.println("Spot IDs: " + spotListaId);
                        System.out.println("Gordea");
                        sort();

                    }
                });
    }

    private void sort() {
        for(String id: spotListaId){
            DocumentReference spotRef = FirebaseFirestore.getInstance().collection("spots").document(id);

            spotRef.get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            if (documentSnapshot.exists()) {
                               String type =  (String) documentSnapshot.get("type");
                                Object num= documentSnapshot.get("number");
                               //System.out.println(num.toString());
                                all.add(new Spota(num.toString(),id,type));
                                proccessType(type,id,num.toString());

                                // Use the retrieved values as needed
                            }

                        } else {
                            // Error occurred while fetching the user document
                            Exception exception = task.getException();
                            // Handle the exception
                        }
                    });
        }
    }

    private void proccessType(String type,String id,String num){
        System.out.println(type);
        if(type.equals("CAR")){
            car.add(new Spota(num,id,type));
        }else if(type.equals("HANDICAPPED")){
            hand.add(new Spota(num,id,type));
        }else if(type.equals("ELECTRIC")){
            elec.add(new Spota(num,id,type));
        }else{
            motor.add(new Spota(num,id,type));
        }
    }

    public String getIdByNum(String num){

        for (Spota spota : all) {
            if (spota.getNum().equals(num)) {
                String id=spota.getId();
                System.out.println(id);
                return id; // Return the id if num matches
            }
        }
        return null; // Return null if no matching num is found

    }
    public String getNumById(String id){

        for (Spota spota : all) {
            if (spota.getId().equals(id)) {
                String num=spota.getNum();
                System.out.println(num);
                return num; // Return the id if num matches
            }
        }
        return null; // Return null if no matching num is found

    }
    public String getTypeById(String id){

        for (Spota spota : all) {
            if (spota.getId().equals(id)) {
                String type=spota.getType();
                System.out.println(type);
                return type; // Return the id if num matches
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

    public class Spota{
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

