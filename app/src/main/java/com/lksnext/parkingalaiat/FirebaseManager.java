package com.lksnext.parkingalaiat;


import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.atomic.AtomicBoolean;

public class FirebaseManager {
    private static final String EXTRA_LOGIN_SUCCESS="com.lksnext.parkingalaiat.EXTRA_LOGIN_SUCCESS";

    private static FirebaseManager instance;
    private FirebaseAuth mAuth;
    private FirebaseDatabase db;
    private AtomicBoolean loginSuccessful=new AtomicBoolean(false);;

    private FirebaseManager(){
        db = FirebaseDatabase.getInstance();
        mAuth=FirebaseAuth.getInstance();

    }
    public static synchronized FirebaseManager getInstance() {
        if (instance == null) {
            instance = new FirebaseManager();
        }
        return instance;
    }

    private FirebaseAuth getmAuth() {
        return mAuth;
    }

    private FirebaseDatabase getDb() {
        return db;
    }
    public DatabaseReference getReference(String path) {
        return db.getReference(path);
    }
    public boolean login(String email, String password) throws Exception{
         loginSuccessful.set(false);

           mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Login successful
                            FirebaseUser user = mAuth.getCurrentUser();
                            // Handle the authenticated user (e.g., proceed to the main screen)
                            //showSuccessDialog();
                            this.loginSuccessful.set(true);

                        } else {
                            this.loginSuccessful.set(false);

                        }
                    });
            Tasks.await(mAuth.signInWithEmailAndPassword(email,password));
           return loginSuccessful.get();

    }


}
