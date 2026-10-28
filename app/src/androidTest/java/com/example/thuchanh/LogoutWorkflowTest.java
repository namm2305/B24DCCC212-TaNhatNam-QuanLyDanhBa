package com.example.thuchanh;

import android.app.Activity;
import android.app.Instrumentation;
import android.os.SystemClock;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import androidx.test.runner.lifecycle.Stage;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class LogoutWorkflowTest {
    private final Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();

    // Dùng accessibility của hệ thống để không phụ thuộc cơ chế tiêm sự kiện của Espresso cũ.
    private AccessibilityNodeInfo node(String value, boolean byId) {
        long deadline = SystemClock.uptimeMillis() + 15000;
        do {
            AccessibilityNodeInfo root = instrumentation.getUiAutomation().getRootInActiveWindow();
            if (root != null) {
                List<AccessibilityNodeInfo> nodes = byId
                        ? root.findAccessibilityNodeInfosByViewId(value)
                        : root.findAccessibilityNodeInfosByText(value);
                for (AccessibilityNodeInfo item : nodes) {
                    if (item.isVisibleToUser() && (byId || value.contentEquals(item.getText()))) return item;
                }
            }
            SystemClock.sleep(50);
        } while (SystemClock.uptimeMillis() < deadline);
        throw new AssertionError("Không tìm thấy: " + value);
    }
    private void clickId(String id) {
        assertTrue(node(id, true).performAction(AccessibilityNodeInfo.ACTION_CLICK));
        instrumentation.waitForIdleSync();
    }
    @Test public void cancelConfirmClearBackStackAndPreserveContacts() {
        android.accessibilityservice.AccessibilityServiceInfo info =
                instrumentation.getUiAutomation().getServiceInfo();
        info.flags |= android.accessibilityservice.AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS;
        instrumentation.getUiAutomation().setServiceInfo(info);
        ContactRepository repository = ContactRepository.getInstance();
        String phone = "0800000000";
        while (repository.hasPhoneNumber(phone)) {
            phone = String.format(java.util.Locale.ROOT, "%010d", Long.parseLong(phone) + 1);
        }
        Contact added = new Contact("Kiểm thử đăng xuất", phone, "Khác", "Nam", true, "");
        assertTrue(repository.addContact(added));
        int total = repository.count(), favorites = repository.countFavorites();
        try (ActivityScenario<MainActivity> main = ActivityScenario.launch(MainActivity.class)) {
            clickId("com.example.thuchanh:id/logoutButton");
            node("Bạn có chắc chắn muốn đăng xuất không?", false);
            assertTrue(node("Hủy", false).performAction(AccessibilityNodeInfo.ACTION_CLICK));
            instrumentation.waitForIdleSync();
            assertEquals(String.valueOf(total), node("com.example.thuchanh:id/total", true).getText().toString());
            assertEquals(Lifecycle.State.RESUMED, main.getState());

            clickId("com.example.thuchanh:id/logoutButton");
            clickId("android:id/button1");
            node("com.example.thuchanh:id/loginButton", true);
            assertEquals(Lifecycle.State.DESTROYED, main.getState());
            assertSame(added, repository.findById(added.getId()));
            assertEquals(total, repository.count());
            assertEquals(favorites, repository.countFavorites());

            instrumentation.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
            instrumentation.waitForIdleSync();
            AtomicBoolean mainResumed = new AtomicBoolean(false);
            instrumentation.runOnMainSync(() -> {
                for (Activity activity : ActivityLifecycleMonitorRegistry.getInstance()
                        .getActivitiesInStage(Stage.RESUMED)) {
                    if (activity instanceof MainActivity) mainResumed.set(true);
                }
            });
            assertFalse(mainResumed.get());

            try (ActivityScenario<LoginActivity> login = ActivityScenario.launch(LoginActivity.class)) {
                login.onActivity(activity -> {
                    ((android.widget.EditText) activity.findViewById(R.id.username)).setText("admin");
                    ((android.widget.EditText) activity.findViewById(R.id.password)).setText("123456");
                    activity.findViewById(R.id.loginButton).performClick();
                });
                AuthTestWait.untilComplete(login, R.id.loginButton);
                assertEquals(String.valueOf(total), node("com.example.thuchanh:id/total", true).getText().toString());
                assertEquals(String.valueOf(favorites), node("com.example.thuchanh:id/favorites", true).getText().toString());
                assertSame(added, repository.findById(added.getId()));
            }
        } finally {
            repository.deleteById(added.getId());
        }
    }
}
