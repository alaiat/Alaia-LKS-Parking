package com.lksnext.parkingalaiat;

import android.app.Activity;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.RootMatchers;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;


import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class LoginTest {
    @Before
    public void setUp() {

    }


    @Test
    public void testLogin() {
        ActivityScenario.launch(Login.class);
        Espresso.onView(ViewMatchers.withId(R.id.textEmail))
                .perform(ViewActions.clearText());
        Espresso.onView(ViewMatchers.withId(R.id.textPassword))
                .perform(ViewActions.clearText());

        Espresso.onView(ViewMatchers.withId(R.id.textEmail))
                .perform(ViewActions.typeText("proba@email.com"));
        Espresso.onView(ViewMatchers.withId(R.id.textPassword))
                .perform(ViewActions.typeText("Contrasena1@"));

        Espresso.onView(ViewMatchers.isRoot()).perform(ViewActions.closeSoftKeyboard());


        Espresso.onView(ViewMatchers.withId(R.id.loginButton))
                .perform(ViewActions.click());
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Espresso.onView(ViewMatchers.withId(R.id.seeBookingsLayout))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }
    @Test
    public void testInvalidLogin() {
        ActivityScenario.launch(Login.class);
        Espresso.onView(ViewMatchers.withId(R.id.textEmail))
                .perform(ViewActions.clearText());
        Espresso.onView(ViewMatchers.withId(R.id.textPassword))
                .perform(ViewActions.clearText());


        Espresso.onView(ViewMatchers.withId(R.id.textEmail))
                .perform(ViewActions.typeText("invalid@email.com"));
        Espresso.onView(ViewMatchers.withId(R.id.textPassword))
                .perform(ViewActions.typeText("InvalidPassword123"));

        Espresso.onView(ViewMatchers.isRoot()).perform(ViewActions.closeSoftKeyboard());


        Espresso.onView(ViewMatchers.withId(R.id.loginButton))
                .perform( ViewActions.click());


        Espresso.onView(ViewMatchers.withText("Could not login"))
                .inRoot(RootMatchers.isDialog())
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        Espresso.onView(ViewMatchers.withText("The email or password introduced are wrong. Please check and try again"))
                .inRoot(RootMatchers.isDialog())
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

    }

}
