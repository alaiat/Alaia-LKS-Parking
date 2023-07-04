package com.lksnext.parkingalaiat;

import android.os.Bundle;
import android.view.Menu;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.Query;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.lksnext.parkingalaiat.domain.User;
import com.lksnext.parkingalaiat.domain.UserContext;

public class Perfil extends AppCompatActivity {
    private ImageView image;
    private TextInputLayout name,phone,email;
    private Button edit,save;
    private String nameD,phoneD;
    private FirebaseUser user=UserContext.getInstance().getCurrentUser();
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
        String pn=phone.getEditText().getText().toString();

        if(na.isEmpty()){
            name.setError(" ");
            error=true;
        }

        if(pn.isEmpty()){
            phone.setError(" ");
            error=true;

        }
        if(!error){
            nameD=na;
            phoneD=pn;
            updateUserData(user.getUid(),nameD,phoneD);
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

        showData();








    }

    private void showData() {
        DocumentReference userRef = FirebaseFirestore.getInstance().collection("users").document(user.getUid());

        userRef.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        if (documentSnapshot.exists()) {
                            String name1 = documentSnapshot.getString("name");
                            String phoneNumber = documentSnapshot.getString("phoneNumber");
                            String email1 = documentSnapshot
                                    .getString("email");

                            System.out.println(phoneNumber+"\n\n\n\n\n\n\n\n");
                            this.name.getEditText().setText(name1);
                            this.phone.getEditText().setText(phoneNumber);
                            this.email.getEditText().setText(email1);

                            // Use the retrieved values as needed
                        } else {
                            // User document does not exist
                        }
                    } else {
                        // Error occurred while fetching the user document
                        Exception exception = task.getException();
                        // Handle the exception
                    }
                });
    }


    public void updateUserData(String userId, String newName, String newPhoneNumber) {
        DocumentReference userRef =  FirebaseFirestore.getInstance().collection("users").document(userId);

        userRef.update("name", newName, "phoneNumber", newPhoneNumber)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // User data update successful
                        // Proceed with next steps or show a success message
                    } else {
                        // User data update failed
                        Exception exception = task.getException();
                        // Handle the exception
                    }
                });
    }
}
