package com.lksnext.parkingalaiat.domain;

import com.google.firebase.auth.FirebaseUser;

public class UserContext {
    private static UserContext instance;
    private FirebaseUser currentUser;

    private UserContext() {
        // Private constructor to prevent direct instantiation
    }

    public static synchronized UserContext getInstance() {
        if (instance == null) {
            instance = new UserContext();
        }
        return instance;
    }

    public FirebaseUser getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(FirebaseUser user) {
        currentUser = user;
    }
}
