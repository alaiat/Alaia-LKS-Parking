package com.lksnext.parkingalaiat;

import static android.content.ContentValues.TAG;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {





    private Button button;
    private TextView link;


    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private String errorDialogTitle="";
    private String errorDialogBody="";
    private Context context=this;
    private TextInputLayout name;
    private TextInputEditText edName;
    private TextInputLayout email;
    private TextInputEditText edEmail;
    private TextInputLayout phoneNumber;
    private TextInputEditText edPhoneNumber;
    private TextInputLayout password;
    private TextInputEditText edPassword;

    private TextInputLayout secondPassword;
    private TextInputEditText edSecondPassword;

    private LinearProgressIndicator progressIndicator;









    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        mAuth=FirebaseAuth.getInstance();
        db=FirebaseFirestore.getInstance();
        initUi();

    }

    private void initUi() {
        initView();
        initListeners();
    }


    private void initView() {
        button=findViewById(R.id.registerButton);
        link=findViewById(R.id.loginLink);
        //add the stroke to the link
        SpannableString content = new SpannableString(link.getText());
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        link.setText(content);





        name =findViewById(R.id.nameField);
        edName = (TextInputEditText) name.getEditText();

        email=findViewById(R.id.emailField);
        edEmail= (TextInputEditText) email.getEditText();

        phoneNumber=findViewById(R.id.phoneNumber);
        edPhoneNumber= (TextInputEditText) phoneNumber.getEditText();

        password=findViewById(R.id.password1Field);
        edPassword= (TextInputEditText) password.getEditText();

        secondPassword=findViewById(R.id.password2Field);
        edSecondPassword= (TextInputEditText) secondPassword.getEditText();

        progressIndicator=findViewById(R.id.progressIndicator);
        progressIndicator.setProgressCompat(0, true);
        progressIndicator.setIndicatorColor(getResources().getColor(R.color.red));




    }

    private void initListeners(){
        button.setOnClickListener(v ->{ register(); });
        link.setOnClickListener(v ->{changeToLogin();});
        // Get input text

        //name.setOnClickListener(view -> {editarTexto();});

        //password.setOnClickListener(view -> {onPasswordEdit();});
        edPassword.setOnClickListener(view -> {onPasswordEdit();});
    }

    private void onPasswordEdit() {
        System.out.println("Sartu\n\n\n\n");
        if (edPassword != null) {
            edPassword.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    // Do nothing
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    // Update the progress of the LinearProgressIndicator
                    String password = s.toString();
                    System.out.println(password+"\n\n\n\n");
                    int progress = calculateProgress(password); // Calculate the progress based on password complexity or criteria
                    progressIndicator.setProgressCompat(progress, true);
                }

                @Override
                public void afterTextChanged(Editable s) {
                    // Do nothing
                }
            });
        }

    }
    private int calculateProgress(String password) {
        int progress = 0;

        // Check for special character
        if (password.matches(".*[!@#$%^&*()_+=\\-\\[\\]{};':\"\\\\|,.<>\\/?].*")) {
            progress += 20;
        }

        // Check for lowercase letter
        if (password.matches(".*[a-z].*")) {
            progress += 20;
        }

        // Check for uppercase letter
        if (password.matches(".*[A-Z].*")) {
            progress += 20;
        }

        // Check for number
        if (password.matches(".*\\d.*")) {
            progress += 20;
        }

        // Check for minimum length of 6
        if (password.length() >= 6) {
            progress += 20;
        }

        if(progress==100){
            progressIndicator.setIndicatorColor(getResources().getColor(R.color.teal_200));
        }

        return progress;
    }



    private void register(){
        String nam = name.getEditText().getText().toString();
        String ema = email.getEditText().getText().toString();
        String pn = phoneNumber.getEditText().getText().toString();
        String pass=password.getEditText().getText().toString();
        String pass2=secondPassword.getEditText().getText().toString();

        if(nam.isEmpty() | ema.isEmpty()|pn.isEmpty()|pass.isEmpty()|pass2.isEmpty()){
            showErrorDialog("Missing info","You must fill all the data");
        }else{
            if(makeAllRegisterCheckings(nam,ema,pn,pass,pass2)) {
                mAuth.createUserWithEmailAndPassword(ema, pass)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                String userId = mAuth.getCurrentUser().getUid();
                                //saveUserDetails(userId, nam, ema, pn);
                                showSuccessDialog();
                                saveUserData(userId, ema, nam, pn);

                            } else {
                                try {
                                    throw task.getException();
                                } catch (FirebaseAuthInvalidCredentialsException invalidCredentialsException) {
                                    showErrorDialog("Error","The email must have a valid email format");
                                } catch (FirebaseAuthUserCollisionException userCollisionException) {
                                    showErrorDialog("Error", "Email already is linked with an account.");
                                } catch (Exception e) {
                                    showErrorDialog("Error", "Error creating the user.");
                                }
                            }
                        });

            }else{
                System.out.println("malmalmal");
            }
        }


    }

    private void changeToLogin(){
        Intent intent = new Intent(Register.this, Login.class);
        startActivity(intent);
    }


    private boolean makeNameAndPhoneNumberCheckings(String name, String phoneNumber){
        if(checkPhoneNumber(phoneNumber) && checkNameValid(name)){
            return true;
        }else{
            showErrorDialog(errorDialogTitle,errorDialogBody);
            return false;
        }
    }
    private boolean makeAllRegisterCheckings(String name, String email, String phoneN,String passW,String pass2){
        boolean isEverythingCorrect= checkEveryThingWithPasswords(passW,pass2) && isEmailValid(email) && makeNameAndPhoneNumberCheckings(name,phoneN);
        return isEverythingCorrect;
    }
    private boolean checkPasswordsAreTheSame(String pass1, String pass2) {
        if(pass1.equals(pass2)){
            return true;
        }else{
            showErrorDialog(null,"Passwords must be the same");
            return false;
        }

    }
    private boolean checkEveryThingWithPasswords(String pass1,String pass2){
        return checkPasswordsAreTheSame(pass1,pass2) && isPasswordValid(pass1);
    }
    private boolean isEmailValid(String email){
        if(!email.isEmpty()){
            return true;
        }else{
            showErrorDialog(null,"Email can't be empty.");
            return false;
        }
    }

    private boolean isPasswordValid(String password) {
        boolean hasLength = (password.length() >= 6);
        boolean hasSpecialChar = containsSpecialCharacter(password);
        boolean hasNumber = containsNumber(password);
        boolean hasUppercaseAndLowercase = containsUppercaseAndLowercase(password);
        if(hasLength && hasSpecialChar && hasNumber && hasUppercaseAndLowercase){
            return true;
        }else{
            showErrorDialog("Error", "Passwords must have at least 6 characters. One Uppercase, one lowercase, a numeric one and a special one.");
            return false;
        }


    }

    private boolean checkNameValid(String name){
        if(!name.isEmpty()){
            return true;
        }else{
            errorDialogTitle="Error with the name!";
            errorDialogBody="Name can not be empty";
            return false;
        }

    }
    private boolean checkPhoneNumber(String number){
        if(!number.isEmpty()){
            return true;
        }else{
            errorDialogTitle="Error with the phone number!";
            errorDialogBody="Phone number can not be empty";
            return false;
        }
    }

    private boolean containsNumber(String str) {
        // Check if the string matches the regex pattern for numbers
        return str.matches(".*\\d.*");
    }
    public boolean containsUppercaseAndLowercase(String str) {
        // Check if the string contains at least one uppercase letter and one lowercase letter
        return str.matches("(?=.*[A-Z])(?=.*[a-z]).*");
    }
    private boolean containsSpecialCharacter(String str) {
        // Define a regex pattern for special characters
        String specialChars = "[!@#$%^&*(),.?\":{}|<>]";

        // Check if the string matches the regex pattern
        return str.matches(".*" + specialChars + ".*");
    }
    /*
    private void saveUserDetails(String userId, String name, String email, String phoneNumber) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");

        User newUser = new User(userId, name, email, phoneNumber);
        usersRef.child(userId).setValue(newUser)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // User details saved successfully
                        System.out.println("User data saved successfully");
                    } else {
                        // Failed to save user details
                        // Handle error
                        System.out.println("Error saving user data");
                    }
                });
    }*/







    private void showSuccessDialog() {
        new MaterialAlertDialogBuilder(context)
                .setTitle("User created!")
                .setMessage("You will be redirected to the login page.")
                .setPositiveButton("OK", (dialog, which) -> {
                    // Handle positive button click
                    dialog.dismiss();
                    changeToLogin();
                })
                .show();
    }
    private void showErrorDialog(String title,String message) {
        new MaterialAlertDialogBuilder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> {
                    // Handle positive button click
                    dialog.dismiss();
                })
                .show();
    }





    private void saveUserData(String userId, String email, String name, String phoneNumber) {
        // Create a new user with a first and last name
        Map<String, Object> user = new HashMap<>();
        user.put("first", "Ada");
        user.put("last", "Lovelace");
        user.put("born", 1815);

// Add a new document with a generated ID
        db.collection("users")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }


}
