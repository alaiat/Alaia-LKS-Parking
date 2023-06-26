package com.lksnext.parkingalaiat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.lksnext.parkingalaiat.domain.ParkingLot;
import com.lksnext.parkingalaiat.domain.Spot;
import com.lksnext.parkingalaiat.domain.User;

import java.util.List;


public class MainActivity extends AppCompatActivity {

    public ParkingLot parking;
    private List<User> userList;
    private Button loginB;
    private Button registerB;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Intent intent = new Intent(MainActivity.this, Login.class);
        startActivity(intent);
        initUi();

    }

    private void initUi() {
        initView();
        initListeners();
        //initializeDatabase();
    }

    private void initializeDatabase() {
        parking=new ParkingLot("parkina","helbidea");




/*
        Spot spot0=new Spot(0, Spot.SpotType.CAR);
        Spot spot1=new Spot(1, Spot.SpotType.CAR);
        Spot spot2=new Spot(2, Spot.SpotType.ELECTRIC);
        Spot spot3=new Spot(3, Spot.SpotType.ELECTRIC);
        Spot spot4=new Spot(4, Spot.SpotType.HANDICAPPED);
        Spot spot5=new Spot(5, Spot.SpotType.HANDICAPPED);
        Spot spot6=new Spot(6, Spot.SpotType.MOTORCYCLE);
        Spot spot7=new Spot(7, Spot.SpotType.MOTORCYCLE);

        parking.addSpot(spot0);
        parking.addSpot(spot1);
        parking.addSpot(spot2);
        parking.addSpot(spot3);
        parking.addSpot(spot4);
        parking.addSpot(spot5);
        parking.addSpot(spot6);
        parking.addSpot(spot7);

*/

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