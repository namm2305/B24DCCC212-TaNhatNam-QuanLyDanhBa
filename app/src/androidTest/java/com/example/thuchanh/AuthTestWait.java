package com.example.thuchanh;

import android.os.SystemClock;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import java.util.concurrent.atomic.AtomicBoolean;
import static org.junit.Assert.fail;

final class AuthTestWait {
    private AuthTestWait() {}
    // Chờ tác vụ PBKDF2 thực sự kết thúc; không dùng thời gian ngủ cố định.
    static <A extends AppCompatActivity> void untilComplete(ActivityScenario<A> scenario, int buttonId) {
        long deadline = SystemClock.uptimeMillis() + 60000;
        while (SystemClock.uptimeMillis() < deadline) {
            if (scenario.getState() == Lifecycle.State.DESTROYED) return;
            AtomicBoolean idle = new AtomicBoolean(false);
            scenario.onActivity(activity -> idle.set(activity.findViewById(buttonId).isEnabled()));
            if (idle.get()) return;
            SystemClock.sleep(20);
        }
        fail("Tác vụ xác thực không hoàn tất trong 60 giây");
    }
}
