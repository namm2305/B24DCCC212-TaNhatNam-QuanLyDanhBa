package com.example.thuchanh;

import android.content.Intent;
import android.content.IntentFilter;
import androidx.lifecycle.Lifecycle;
import android.app.Instrumentation;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import org.junit.Test;
import org.junit.runner.RunWith;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBackUnconditionally;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.*;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class ContactWorkflowTest {
    @Test public void completeContactWorkflow() {
        try (ActivityScenario<LoginActivity> login = ActivityScenario.launch(LoginActivity.class)) {
            onView(withId(R.id.loginButton)).perform(scrollTo(), click());
            onView(withId(R.id.username)).check(matches(hasErrorText("Vui lòng nhập tên đăng nhập")));
            onView(withId(R.id.username)).perform(replaceText("wrong"), closeSoftKeyboard());
            onView(withId(R.id.password)).perform(replaceText("wrong"), closeSoftKeyboard());
            onView(withId(R.id.loginButton)).perform(scrollTo(), click());
            AuthTestWait.untilComplete(login, R.id.loginButton);
            onView(withId(R.id.loginError)).check(matches(withText("Sai tên đăng nhập hoặc mật khẩu")));
            onView(withId(R.id.username)).perform(replaceText("admin"), closeSoftKeyboard());
            onView(withId(R.id.password)).perform(replaceText("123456"), closeSoftKeyboard());
            onView(withId(R.id.loginButton)).perform(scrollTo(), click());
            AuthTestWait.untilComplete(login, R.id.loginButton);
            assertEquals(Lifecycle.State.DESTROYED, login.getState());
            onView(withId(R.id.total)).check(matches(withText("3")));
            onView(withId(R.id.favorites)).check(matches(withText("1")));
            onView(withText("Nguyễn Văn An")).check(matches(isDisplayed()));
            onView(withText("Trần Thị Bình")).check(matches(isDisplayed()));
            onView(withText("Lê Minh Châu")).check(matches(isDisplayed()));
            onView(withId(R.id.addButton)).perform(click());
            onView(withId(R.id.saveButton)).perform(scrollTo(), click());
            onView(withId(R.id.fullName)).check(matches(hasErrorText("Họ tên không được bỏ trống")));
            onView(withId(R.id.fullName)).perform(scrollTo(), replaceText("Test Contact"), closeSoftKeyboard());
            for (String phone : new String[]{"123", "1901234567", "09012345678"}) {
                onView(withId(R.id.phone)).perform(scrollTo(), replaceText(phone), closeSoftKeyboard());
                onView(withId(R.id.saveButton)).perform(scrollTo(), click());
                onView(withId(R.id.phone)).check(matches(hasErrorText("Số điện thoại phải gồm đúng 10 chữ số và bắt đầu bằng 0")));
            }
            onView(withId(R.id.phone)).perform(scrollTo(), replaceText("0901234567"), closeSoftKeyboard());
            onView(withId(R.id.saveButton)).perform(scrollTo(), click());
            onView(withId(R.id.phone)).check(matches(hasErrorText("Số điện thoại đã có trong danh bạ")));
            onView(withId(R.id.fullName)).check(matches(withText("Test Contact")));
            onView(withId(R.id.phone)).perform(scrollTo(), replaceText("0900000000"), closeSoftKeyboard());
            onView(withId(R.id.male)).check(matches(isChecked()));
            onView(withId(R.id.favorite)).perform(scrollTo(), click());
            onView(withId(R.id.note)).perform(scrollTo(), replaceText("Test note"), closeSoftKeyboard());
            onView(withId(R.id.saveButton)).perform(scrollTo(), click());
            onView(withId(R.id.total)).check(matches(withText("4")));
            onView(withId(R.id.favorites)).check(matches(withText("2")));
            onView(withText("Test Contact")).perform(click());
            onView(withId(R.id.contactNote)).check(matches(withText("Test note")));
            onView(withId(R.id.favorite)).check(matches(isChecked())).perform(scrollTo(), click());
            onView(withId(R.id.backButton)).perform(scrollTo(), click());
            onView(withId(R.id.favorites)).check(matches(withText("1")));
            onView(withText("Test Contact")).perform(click());
            onView(withId(R.id.deleteButton)).perform(scrollTo(), click());
            onView(withText("Hủy")).perform(click());
            onView(withId(R.id.contactName)).check(matches(withText("Test Contact")));
            onView(withId(R.id.deleteButton)).perform(scrollTo(), click());
            onView(withText("Xóa")).perform(click());
            onView(withId(R.id.total)).check(matches(withText("3")));
            onView(withId(R.id.favorites)).check(matches(withText("1")));
            onView(withText("Nguyễn Văn An")).perform(click());
            // Chặn Intent trong test để không phụ thuộc giao diện ứng dụng quay số.
            Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
            IntentFilter filter = new IntentFilter(Intent.ACTION_DIAL);
            filter.addDataScheme("tel");
            Instrumentation.ActivityMonitor monitor = instrumentation.addMonitor(
                    filter, new Instrumentation.ActivityResult(0, null), true);
            try {
                onView(withId(R.id.callButton)).perform(scrollTo(), click());
                assertEquals(1, monitor.getHits());
            } finally {
                instrumentation.removeMonitor(monitor);
            }
            onView(withId(R.id.backButton)).perform(scrollTo(), click());
            onView(withId(R.id.total)).check(matches(withText("3")));
            // Xóa hết để kiểm tra trạng thái rỗng và dữ liệu mẫu không tự sinh lại.
            for (String name : new String[]{"Nguyễn Văn An", "Trần Thị Bình", "Lê Minh Châu"}) {
                onView(withText(name)).perform(click());
                onView(withId(R.id.deleteButton)).perform(scrollTo(), click());
                onView(withText("Xóa")).perform(click());
            }
            onView(withId(R.id.total)).check(matches(withText("0")));
            onView(withId(R.id.favorites)).check(matches(withText("0")));
            onView(withId(R.id.empty)).check(matches(isDisplayed()));
            pressBackUnconditionally();
        }
    }
}
