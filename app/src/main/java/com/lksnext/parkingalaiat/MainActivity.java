package com.lksnext.parkingalaiat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.lksnext.parkingalaiat.domain.CurrentParking;
import com.lksnext.parkingalaiat.domain.ParkingLot;
import com.lksnext.parkingalaiat.domain.ReservaOld;
import com.lksnext.parkingalaiat.domain.Spot;
import com.lksnext.parkingalaiat.domain.User;
import com.lksnext.parkingalaiat.domain.UserContext;

import org.checkerframework.checker.units.qual.C;

import java.util.List;
import java.util.UUID;


public class MainActivity extends AppCompatActivity {
    private CurrentParking current;
    private Button loginB;
    private Button registerB;
    private FirebaseFirestore db;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        current= CurrentParking.getInstance();
        Intent intent = new Intent(MainActivity.this, Login.class);
        startActivity(intent);

        initUi();




    }

    private void initUi() {
        initView();
        initListeners();
        //initializeSpots();



    }



    private void initializeSpots() {

        db=FirebaseFirestore.getInstance();
        CollectionReference spotsCollection = db.collection("spots");
        List<Spot> spotsList = new ArrayList<>();
        spotsList.add(new Spot("CAR",1));
        spotsList.add(new Spot("CAR",2));
        spotsList.add(new Spot("CAR",3));
        spotsList.add(new Spot("CAR",4));
        spotsList.add(new Spot("CAR",5));
        spotsList.add(new Spot("MOTORCYCLE",6));
        spotsList.add(new Spot("HANDICAPPED",7));
        spotsList.add(new Spot("ELECTRIC",8));


        for (Spot spot : spotsList) {
            // Generate a unique spot ID (or use spot number as the document ID)
            String spotId = UUID.randomUUID().toString();

            // Create a document reference for the spot using the spot ID
            DocumentReference spotRef = db.collection("spots").document(spotId);

            // Set the spot data to the document
            spotRef.set(spot)
                    .addOnSuccessListener(aVoid -> {
                        // Spot data saved successfully
                        System.out.println("Spot saved: " + spotId);
                    })
                    .addOnFailureListener(e -> {
                        // Error occurred while saving spot data
                        System.out.println("Failed to save spot: " + spotId);
                        System.out.println(e.toString());
                    });
        }

    }

    private void initView() {
        loginB=findViewById(R.id.buttonLogin);
        registerB=findViewById(R.id.buttonRegister);
    }

    private void initListeners(){
        loginB.setOnClickListener(v ->{ changeToLogin(); });
        registerB.setOnClickListener(v ->{changeToRegister();});
    }

    private void changeToLogin(){
        Intent intent = new Intent(MainActivity.this, Login.class);
        startActivity(intent);
    }
    private void changeToRegister(){
        //Intent intent = new Intent(MainActivity.this, Register.class);
        //startActivity(intent);
        changeToNuevaReserva();
    }
    private void changeToNuevaReserva() {
        DialogFragment dialog = NuevaReserva.newInstance();
        ((NuevaReserva) dialog).setCallback(new NuevaReserva.Callback() {
            @Override
            public void onActionClick(String name) {
                Toast.makeText(MainActivity.this, name, Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show(getSupportFragmentManager(), "tag");
    }
}