package com.lksnext.parkingalaiat;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Login extends AppCompatActivity {

    private Button button;
    private TextView email;
    private TextView password;
    private TextView link;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        initUi();
    }

    private void initUi() {
        initView();
        initListeners();
    }

    private void initView() {
        button=findViewById(R.id.loginButton);
        email=findViewById(R.id.email);
        password=findViewById(R.id.password);
        link=findViewById(R.id.link);
    }

    private void initListeners(){
        button.setOnClickListener(v ->{ login(); });
        link.setOnClickListener(view -> {changeToRegister();});
    }

    private void login(){

    }
    private void changeToRegister(){
        Intent intent = new Intent(Login.this, Register.class);
        startActivity(intent);
    }
}