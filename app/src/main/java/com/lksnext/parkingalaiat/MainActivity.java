package com.lksnext.parkingalaiat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.lksnext.parkingalaiat.domain.ParkingLot;
import com.lksnext.parkingalaiat.domain.Reserva;
import com.lksnext.parkingalaiat.domain.Spot;
import com.lksnext.parkingalaiat.domain.User;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ParkingLot parking;
    private List<User> userList;



    private Button loginB;
    private Button registerB;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(MainActivity.this, VerReservas.class);
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

        User user1=new User("user1","user1@email.com");
        User user2=new User("user2","user2@email.com");
        User user3=new User("user3","user3@email.com");

        userList.add(user1);
        userList.add(user2);
        userList.add(user3);
        System.out.println("Lista: "+userList.toString());



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

        // Get the current date and time
       /* Calendar calendar = Calendar.getInstance();

        // Set the time for the first date: today at 12:30
        calendar.set(Calendar.HOUR_OF_DAY, 12);
        calendar.set(Calendar.MINUTE, 30);
        calendar.set(Calendar.SECOND, 0);
        Date date1 = calendar.getTime();

        // Set the time for the second date: today at 19:00
        calendar.set(Calendar.HOUR_OF_DAY, 19);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Date date2 = calendar.getTime();

        Reserva r1=new Reserva(user1,spot0,date1,date2);*/

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
        Intent intent = new Intent(MainActivity.this, Register.class);
        startActivity(intent);
    }
}