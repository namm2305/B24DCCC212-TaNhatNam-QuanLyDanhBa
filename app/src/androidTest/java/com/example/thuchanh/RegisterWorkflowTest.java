package com.example.thuchanh;

import android.content.Context;
import android.util.Base64;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import org.junit.Test;
import org.junit.runner.RunWith;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;

@RunWith(AndroidJUnit4.class)
public class RegisterWorkflowTest {
    @Test public void validatesRegistrationAndAllowsRegisteredLogin() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        String username = "ui_" + java.util.UUID.randomUUID();
        String key = "account_" + Base64.encodeToString(username.getBytes(java.nio.charset.StandardCharsets.UTF_8),
                Base64.NO_WRAP | Base64.URL_SAFE);
        try (ActivityScenario<LoginActivity> login = ActivityScenario.launch(LoginActivity.class)) {
            onView(withId(R.id.registerLink)).perform(scrollTo(), click());
            onView(withId(R.id.registerButton)).perform(scrollTo(), click());
            onView(withId(R.id.registerFullName)).check(matches(hasErrorText(context.getString(R.string.name_required))));
            onView(withId(R.id.registerFullName)).perform(scrollTo(), replaceText("Test User"), closeSoftKeyboard());
            onView(withId(R.id.registerButton)).perform(scrollTo(), click());
            onView(withId(R.id.registerUsername)).check(matches(hasErrorText(context.getString(R.string.username_required))));
            onView(withId(R.id.registerUsername)).perform(scrollTo(), replaceText("admin"), closeSoftKeyboard());
            onView(withId(R.id.registerButton)).perform(scrollTo(), click());
            onView(withId(R.id.registerPassword)).check(matches(hasErrorText(context.getString(R.string.password_required))));
            onView(withId(R.id.registerPassword)).perform(scrollTo(), replaceText("123"), closeSoftKeyboard());
            onView(withId(R.id.confirmPassword)).perform(scrollTo(), replaceText("123"), closeSoftKeyboard());
            onView(withId(R.id.registerButton)).perform(scrollTo(), click());
            onView(withId(R.id.registerPassword)).check(matches(hasErrorText(context.getString(R.string.password_short))));
            onView(withId(R.id.registerPassword)).perform(scrollTo(), replaceText("secret123"), closeSoftKeyboard());
            onView(withId(R.id.registerButton)).perform(scrollTo(), click());
            onView(withId(R.id.confirmPassword)).check(matches(hasErrorText(context.getString(R.string.password_mismatch))));
            onView(withId(R.id.confirmPassword)).perform(scrollTo(), replaceText("secret123"), closeSoftKeyboard());
            onView(withId(R.id.registerButton)).perform(scrollTo(), click());
            onView(withId(R.id.registerUsername)).check(matches(hasErrorText(context.getString(R.string.username_duplicate))));
            onView(withId(R.id.registerUsername)).perform(scrollTo(), replaceText(username), closeSoftKeyboard());
            onView(withId(R.id.registerButton)).perform(scrollTo(), click());
            // Theo dõi qua màn hình Login: Espresso chỉ tiếp tục khi Login trở lại.
            long deadline = android.os.SystemClock.uptimeMillis() + 60000;
            while (login.getState() != androidx.lifecycle.Lifecycle.State.RESUMED
                    && android.os.SystemClock.uptimeMillis() < deadline) {
                android.os.SystemClock.sleep(20);
            }
            onView(withId(R.id.username)).check(matches(withText(username)));
            onView(withId(R.id.password)).perform(scrollTo(), replaceText("secret123"), closeSoftKeyboard());
            onView(withId(R.id.loginButton)).perform(scrollTo(), click());
            AuthTestWait.untilComplete(login, R.id.loginButton);
            onView(withId(R.id.addButton)).check(matches(isDisplayed()));
        } finally {
            context.getSharedPreferences("registered_accounts", Context.MODE_PRIVATE).edit().remove(key).commit();
        }
    }
}
