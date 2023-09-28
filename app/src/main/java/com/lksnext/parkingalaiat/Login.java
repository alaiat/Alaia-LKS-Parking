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

    private Button button;
    private TextInputEditText editEmail;
    private TextInputLayout password;
    private TextInputEditText editPassword;
    private TextView createAccount;
    private FirebaseAuth mAuth;
    private final Context context = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        mAuth = FirebaseAuth.getInstance();
        initUi();
    }

    private void initUi() {
        initView();
        initListeners();
    }

    private void initView() {
        button = findViewById(R.id.loginButton);
        TextInputLayout email = findViewById(R.id.email);
        editEmail = (TextInputEditText) email.getEditText();

        password = findViewById(R.id.password);
        editPassword = (TextInputEditText) password.getEditText();

        TextView changePassword = findViewById(R.id.changePassword);
        SpannableString content = new SpannableString(changePassword.getText());
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        changePassword.setText(content);

        createAccount = findViewById(R.id.createAccount);
        SpannableString content2 = new SpannableString(createAccount.getText());
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        createAccount.setText(content2);

        setTitle("");
    }

    private void initListeners() {
        button.setOnClickListener(v -> {
            login();
        });
        createAccount.setOnClickListener(view -> {
            changeToRegister();
        });
        editPassword.setOnClickListener(view -> {
            password.setError(null);
        });
    }


    public void login() {

        String em = editEmail.getText().toString();
        String pass = editPassword.getText().toString();
        try {
            mAuth.signInWithEmailAndPassword(em, pass)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser actualUser = mAuth.getCurrentUser();
                            UserContext.getInstance().setCurrentUser(actualUser);
                            changeToSeeBookings();
                        } else {
                            showErrorDialog();

                        }
                    });
        } catch (Exception e) {
            showErrorDialog();
        }

    }

    private void changeToSeeBookings() {
        Intent intent = new Intent(Login.this, SeeBookings.class);
        startActivity(intent);
        password.setError(null);
    }

    private void showErrorDialog() {
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

    public void changeToRegister() {
        Intent intent = new Intent(Login.this, Register.class);
        startActivity(intent);
        password.setError(null);
    }
}