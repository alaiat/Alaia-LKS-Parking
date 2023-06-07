package com.lksnext.parkingalaiat;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Register extends AppCompatActivity {

    private Button button;
    private TextView email;
    private TextView password;
    private TextView username;
    private TextView link;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        initUi();
    }

    private void initUi() {
        initView();
        initListeners();
    }

    private void initView() {
        button=findViewById(R.id.registerButton);
        email=findViewById(R.id.emailField);
        password=findViewById(R.id.passwordField);
        link=findViewById(R.id.loginLink);
    }

    private void initListeners(){
        button.setOnClickListener(v ->{ register(); });
        link.setOnClickListener(v ->{changeToLogin();});
    }

    private void register(){

    }
    private void changeToLogin(){
        Intent intent = new Intent(Register.this, Login.class);
        startActivity(intent);
    }
}
