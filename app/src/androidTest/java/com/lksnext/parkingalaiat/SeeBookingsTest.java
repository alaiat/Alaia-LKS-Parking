package com.lksnext.parkingalaiat;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.lksnext.parkingalaiat.domain.UserContext;

import org.junit.Before;
import org.junit.Test;

public class SeeBookingsTest {
    @Before
    public void setUp() {
        // Log in a user before running the test
        // You may need to replace this with actual login code for your app
        // For testing, you might consider using Firebase Authentication Test Rules
        // to set up a test user.
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        if (user == null) {
            auth.signInWithEmailAndPassword("proba@email.com", "Contrasena1@")
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser actualUser = auth.getCurrentUser();
                            UserContext.getInstance().setCurrentUser(actualUser);
                        }
                    });
        }
    }

    @Test
    public void testLogOut() {
        // Launch the activity

        ActivityScenario.launch(SeeBookings.class);

        // Simulate a click on the log-out button
        ViewInteraction logOutButton = Espresso.onView(ViewMatchers.withId(R.id.logout));
        logOutButton.perform(ViewActions.click());

        // Check if the user is logged out
        FirebaseAuth auth = FirebaseAuth.getInstance();
        assert auth.getCurrentUser() == null;
    }
}
