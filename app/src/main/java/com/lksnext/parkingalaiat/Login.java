package com.lksnext.parkingalaiat;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.AuthResult;


public class Login extends AppCompatActivity {

    private Button button;
    private TextInputLayout email;
    private TextInputEditText edEmail;
    private TextInputLayout password;
    private TextInputEditText edPassword;
    private TextView changeP;
    private TextView createAcco;

    private FirebaseAuth mAuth;
    private Context context=this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        mAuth=FirebaseAuth.getInstance();
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
        button.setOnClickListener(v ->{ login(); });
        createAcco.setOnClickListener(view -> {changeToRegister();});
        changeP.setOnClickListener(view -> {changeToChangePassword();});

    }

    private void changeToChangePassword() {
    }

    private void login(){
        try{
            mAuth.signInWithEmailAndPassword(edEmail.getText().toString(), edPassword.getText().toString())
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Login successful
                            FirebaseUser user = mAuth.getCurrentUser();
                            // Handle the authenticated user (e.g., proceed to the main screen)
                            showSuccessDialog();
                        } else {
                            // Login failed
                            // Handle the failure scenario (e.g., display an error message)

                        }
                    });
        }catch(Exception e){
           showErrorDialog();
        }

    }
    private void showErrorDialog(){
        new MaterialAlertDialogBuilder(context)
                .setTitle("Could not login")
                .setMessage("The email or password introduced are wrong. Please check and try again")
                .setPositiveButton("OK", (dialog, which) -> {
                    // Handle positive button click
                    dialog.dismiss();
                    password.setError("");

                })
                .show();
    }
    private void changeToRegister(){
        Intent intent = new Intent(Login.this, Register.class);
        startActivity(intent);
        password.setError(null);
    }
    private void showSuccessDialog() {
        new MaterialAlertDialogBuilder(context)
                .setTitle("Loggin successfull")
                .setMessage("Este dialogo desaparecera y ira directamente a mis reservas, pero la he liado y es paar ver que login funciona.")
                .setPositiveButton("OK", (dialog, which) -> {
                    // Handle positive button click
                    dialog.dismiss();


                })
                .show();
    }
}