package com.lksnext.parkingalaiat;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.RootMatchers;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class RegisterTest {
    @Test
    public void testValidRegistration() {
        String uniqueEmail = EmailGenerator.generateUniqueEmail();
        ActivityScenario.launch(Register.class);

        Espresso.onView(ViewMatchers.withId(R.id.textName))
                .perform(ViewActions.typeText("John Doe"));
        Espresso.onView(ViewMatchers.withId(R.id.textEmail))
                .perform(ViewActions.typeText(uniqueEmail));
        Espresso.onView(ViewMatchers.withId(R.id.textNumber))
                .perform(ViewActions.typeText("1234567890"));
        Espresso.onView(ViewMatchers.withId(R.id.textPassword1))
                .perform(ViewActions.typeText("ValidPassword123@"));
        Espresso.onView(ViewMatchers.isRoot()).perform(ViewActions.closeSoftKeyboard());

        Espresso.onView(ViewMatchers.withId(R.id.textPassword2))
                .perform(ViewActions.typeText("ValidPassword123@"));

        Espresso.onView(ViewMatchers.isRoot()).perform(ViewActions.closeSoftKeyboard());


        Espresso.onView(ViewMatchers.withId(R.id.registerButton))
                .perform(ViewActions.click());

        Espresso.onView(ViewMatchers.withText("User created!"))
                .inRoot(RootMatchers.isDialog())
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

        Espresso.onView(ViewMatchers.withText("OK"))
                .inRoot(RootMatchers.isDialog())
                .perform(ViewActions.click());
        Espresso.onView(ViewMatchers.withId(R.id.loginLayout))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void testInvalidEmail1Registration() {
        ActivityScenario.launch(Register.class);
        // Input invalid registration information
        Espresso.onView(ViewMatchers.withId(R.id.textName))
                .perform(ViewActions.typeText("John Doe"));
        Espresso.onView(ViewMatchers.withId(R.id.textEmail))
                .perform(ViewActions.typeText("not valid email"));
        Espresso.onView(ViewMatchers.withId(R.id.textNumber))
                .perform(ViewActions.typeText("1234567890"));
        Espresso.onView(ViewMatchers.withId(R.id.textPassword1))
                .perform(ViewActions.typeText("ValidPassword123@"));
        Espresso.onView(ViewMatchers.isRoot()).perform(ViewActions.closeSoftKeyboard());

        Espresso.onView(ViewMatchers.withId(R.id.textPassword2))
                .perform(ViewActions.typeText("ValidPassword123@"));

        Espresso.onView(ViewMatchers.isRoot()).perform(ViewActions.closeSoftKeyboard());


        Espresso.onView(ViewMatchers.withId(R.id.registerButton))
                .perform(ViewActions.click());

        Espresso.onView(ViewMatchers.withText("Email error"))
                .inRoot(RootMatchers.isDialog())
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        Espresso.onView(ViewMatchers.withText("The email must have a valid email format"))
                .inRoot(RootMatchers.isDialog())
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

    }
    @Test
    public void testInvalidEmail2Registration() {
        ActivityScenario.launch(Register.class);
        // Input invalid registration information
        Espresso.onView(ViewMatchers.withId(R.id.textName))
                .perform(ViewActions.typeText("John Doe"));
        Espresso.onView(ViewMatchers.withId(R.id.textEmail))
                .perform(ViewActions.typeText("proba@email.com"));
        Espresso.onView(ViewMatchers.withId(R.id.textNumber))
                .perform(ViewActions.typeText("1234567890"));
        Espresso.onView(ViewMatchers.withId(R.id.textPassword1))
                .perform(ViewActions.typeText("ValidPassword123@"));
        Espresso.onView(ViewMatchers.isRoot()).perform(ViewActions.closeSoftKeyboard());

        Espresso.onView(ViewMatchers.withId(R.id.textPassword2))
                .perform(ViewActions.typeText("ValidPassword123@"));

        Espresso.onView(ViewMatchers.isRoot()).perform(ViewActions.closeSoftKeyboard());


        Espresso.onView(ViewMatchers.withId(R.id.registerButton))
                .perform(ViewActions.click());

        Espresso.onView(ViewMatchers.withText("Error"))
                .inRoot(RootMatchers.isDialog())
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        Espresso.onView(ViewMatchers.withText("Email already is linked with an account."))
                .inRoot(RootMatchers.isDialog())
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

    }
    @Test
    public void testInvalidMissingInfoRegistration() {
        ActivityScenario.launch(Register.class);
        // Input invalid registration information
        Espresso.onView(ViewMatchers.withId(R.id.textName))
                .perform(ViewActions.typeText(" "));
        Espresso.onView(ViewMatchers.withId(R.id.textEmail))
                .perform(ViewActions.typeText("email@email.com"));
        Espresso.onView(ViewMatchers.withId(R.id.textNumber))
                .perform(ViewActions.typeText(" "));
        Espresso.onView(ViewMatchers.withId(R.id.textPassword1))
                .perform(ViewActions.typeText("InvalidPassword"));
        Espresso.onView(ViewMatchers.isRoot()).perform(ViewActions.closeSoftKeyboard());

        Espresso.onView(ViewMatchers.withId(R.id.textPassword2))
                .perform(ViewActions.typeText("InvalidPassword1"));

        Espresso.onView(ViewMatchers.isRoot()).perform(ViewActions.closeSoftKeyboard());


        Espresso.onView(ViewMatchers.withId(R.id.registerButton))
                .perform(ViewActions.click());

        Espresso.onView(ViewMatchers.withText("Missing info"))
                .inRoot(RootMatchers.isDialog())
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        Espresso.onView(ViewMatchers.withText("You must fill all the data"))
                .inRoot(RootMatchers.isDialog())
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));



    }

    @Test
    public void testInvalidPassword1Registration() {
        String uniqueEmail = EmailGenerator.generateUniqueEmail();
        ActivityScenario.launch(Register.class);
        // Input invalid registration information
        Espresso.onView(ViewMatchers.withId(R.id.textName))
                .perform(ViewActions.typeText("John Doe"));
        Espresso.onView(ViewMatchers.withId(R.id.textEmail))
                .perform(ViewActions.typeText(uniqueEmail));
        Espresso.onView(ViewMatchers.withId(R.id.textNumber))
                .perform(ViewActions.typeText("111222333"));
        Espresso.onView(ViewMatchers.withId(R.id.textPassword1))
                .perform(ViewActions.typeText("InvalidPassword1"));
        Espresso.onView(ViewMatchers.isRoot()).perform(ViewActions.closeSoftKeyboard());

        Espresso.onView(ViewMatchers.withId(R.id.textPassword2))
                .perform(ViewActions.typeText("InvalidPassword1"));

        Espresso.onView(ViewMatchers.isRoot()).perform(ViewActions.closeSoftKeyboard());


        Espresso.onView(ViewMatchers.withId(R.id.registerButton))
                .perform(ViewActions.click());

        Espresso.onView(ViewMatchers.withText("Error"))
                .inRoot(RootMatchers.isDialog())
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        Espresso.onView(ViewMatchers.withText("Passwords must have at least 6 characters. One Uppercase, one lowercase, a numeric one and a special one."))
                .inRoot(RootMatchers.isDialog())
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));



    }
    @Test
    public void testInvalidPassword2Registration() {
        String uniqueEmail = EmailGenerator.generateUniqueEmail();
        ActivityScenario.launch(Register.class);
        // Input invalid registration information
        Espresso.onView(ViewMatchers.withId(R.id.textName))
                .perform(ViewActions.typeText("John Doe"));
        Espresso.onView(ViewMatchers.withId(R.id.textEmail))
                .perform(ViewActions.typeText(uniqueEmail));
        Espresso.onView(ViewMatchers.withId(R.id.textNumber))
                .perform(ViewActions.typeText("111222333"));
        Espresso.onView(ViewMatchers.withId(R.id.textPassword1))
                .perform(ViewActions.typeText("InvalidPassword"));
        Espresso.onView(ViewMatchers.isRoot()).perform(ViewActions.closeSoftKeyboard());

        Espresso.onView(ViewMatchers.withId(R.id.textPassword2))
                .perform(ViewActions.typeText("InvalidPassword1"));

        Espresso.onView(ViewMatchers.isRoot()).perform(ViewActions.closeSoftKeyboard());


        Espresso.onView(ViewMatchers.withId(R.id.registerButton))
                .perform(ViewActions.click());


        Espresso.onView(ViewMatchers.withText("Passwords must be the same"))
                .inRoot(RootMatchers.isDialog())
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));



    }
}
