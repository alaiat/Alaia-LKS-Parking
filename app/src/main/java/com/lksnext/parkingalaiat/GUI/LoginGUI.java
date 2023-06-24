package com.lksnext.parkingalaiat.GUI;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.lksnext.parkingalaiat.Login;
import com.lksnext.parkingalaiat.R;
import com.lksnext.parkingalaiat.Register;
import com.lksnext.parkingalaiat.VerReservas;

public class LoginGUI extends AppCompatActivity {

    private Login bl;

    private Button button;
    private TextInputLayout email,password;
    private TextInputEditText edEmail, edPassword;
    private TextView changeP;
    private TextView createAcco;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        bl=new Login();
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
        edEmail= (TextInputEditText) email.getEditText();

        password=findViewById(R.id.password);
        edPassword= (TextInputEditText) password.getEditText();

        changeP=findViewById(R.id.changePassword);
        SpannableString content = new SpannableString(changeP.getText());
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        changeP.setText(content);
        createAcco=findViewById(R.id.createAccount);
        SpannableString content2 = new SpannableString(createAcco.getText());
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        createAcco.setText(content2);
    }
    private void initListeners(){
        //button.setOnClickListener(v ->{ bl.login(edEmail.getText().toString(),edPassword.getText().toString()); });
        createAcco.setOnClickListener(view -> {changeToRegister();});
        //changeP.setOnClickListener(view -> {bl.changeToChangePassword();});
        edPassword.setOnClickListener(view ->{password.setError(null);});

    }

    private void changeToVerReservas() {
        //Intent intent = new Intent(Login.this, VerReservas.class);
        //startActivity(intent);
        password.setError(null);
    }
    public void changeToRegister(){
        //Intent intent = new Intent(Login.this, Register.class);
       // startActivity(intent);
        password.setError(null);
    }
}
