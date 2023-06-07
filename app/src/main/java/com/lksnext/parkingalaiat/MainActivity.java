package com.lksnext.parkingalaiat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private Button loginB;
    private Button registerB;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(MainActivity.this, Perfil.class);
        startActivity(intent);
        initUi();
    }

    private void initUi() {
        initView();
        initListeners();
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