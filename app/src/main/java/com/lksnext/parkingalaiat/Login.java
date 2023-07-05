package com.lksnext.parkingalaiat;

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
import com.google.firebase.auth.FirebaseUser;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.lksnext.parkingalaiat.domain.CurrentParking;
import com.lksnext.parkingalaiat.domain.UserContext;


public class Login extends AppCompatActivity {

    CurrentParking current;
    private Button button;
    private TextInputLayout email;
    private TextInputEditText edEmail;
    private TextInputLayout password;
    private TextInputEditText edPassword;
    private TextView changeP;
    private TextView createAcco;
    private FirebaseManager fireManager;
    private FirebaseAuth mAuth;
    private Context context=this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        mAuth=FirebaseAuth.getInstance();
        fireManager=fireManager.getInstance();
        current=CurrentParking.getInstance();
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
        setTitle("");
    }

    private void initListeners(){
        button.setOnClickListener(v ->{ login(); });
        createAcco.setOnClickListener(view -> {changeToRegister();});
        edPassword.setOnClickListener(view ->{password.setError(null);});

    }



    public void login(){

        String em=edEmail.getText().toString();
        String pass=edPassword.getText().toString();
        try{
            mAuth.signInWithEmailAndPassword(em, pass)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser actualUser = mAuth.getCurrentUser();
                            UserContext.getInstance().setCurrentUser(actualUser);
                            changeToVerReservas();
                        } else {
                            showErrorDialog();

                        }
                    });
        }catch(Exception e){
           showErrorDialog();
        }

    }

    private void changeToVerReservas() {
        Intent intent = new Intent(Login.this, VerReservas.class);
        startActivity(intent);
        password.setError(null);
    }

    private void showErrorDialog(){
        new MaterialAlertDialogBuilder(context)
                .setTitle("Could not login")
                .setMessage("The email or password introduced are wrong. Please check and try again")
                .setPositiveButton("OK", (dialog, which) -> {
                    // Handle positive button click
                    dialog.dismiss();
                    password.setError("Incorrect password");

                })
                .show();
    }
    public void changeToRegister(){
        Intent intent = new Intent(Login.this, Register.class);
        startActivity(intent);
        password.setError(null);
    }


}