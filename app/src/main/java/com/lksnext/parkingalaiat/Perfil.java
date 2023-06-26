package com.lksnext.parkingalaiat;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;

public class Perfil extends AppCompatActivity {
    private ImageView image;
    private TextInputLayout name,phone,email;
    private Button edit,save;
    private String nameD,emailD,phoneD;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        initUi();


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){


        setTitle("Perfil");
        return true;
    }

    private void initUi() {
        initView();
        initListeners();
    }

    private void initListeners() {
        edit.setOnClickListener(view ->{enableEdit();});
        save.setOnClickListener(view->{save();});
    }

    private void save() {
        Boolean error=false;
        String na=name.getEditText().getText().toString();
        String ema=email.getEditText().getText().toString();
        String pn=phone.getEditText().getText().toString();

        if(na.isEmpty()){
            name.setError(" ");
            error=true;
        }
        if(ema.isEmpty()){
            email.setError(" ");
            error=true;

        }
        if(pn.isEmpty()){
            phone.setError(" ");
            error=true;

        }
        if(!error){
            nameD=na;
            emailD=ema;
            phoneD=pn;
            name.getEditText().setText(nameD);
            email.getEditText().setText(emailD);
            phone.getEditText().setText(phoneD);
            save.setEnabled(false);
            email.setError(null);
            name.setError(null);
            phone.setError(null);
            name.setEnabled(false);
            email.setEnabled(false);
            phone.setEnabled(false);
        }
    }

    private void enableEdit() {
        name.setEnabled(true);
        email.setEnabled(true);
        phone.setEnabled(true);
        save.setEnabled(true);
    }

    private void initView() {
        image=findViewById(R.id.imageView);
        name=findViewById(R.id.name);
        email=findViewById(R.id.email);
        phone=findViewById(R.id.number);
        edit=findViewById(R.id.edit);
        save=findViewById(R.id.save);

        nameD="Alaia";
        emailD="lks@gmail.com";
        phoneD="666777888";

        name.getEditText().setText(nameD);
        email.getEditText().setText(emailD);
        phone.getEditText().setText(phoneD);
    }
}
