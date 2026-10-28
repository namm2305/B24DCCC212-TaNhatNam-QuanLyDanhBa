package com.example.thuchanh;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.nio.charset.StandardCharsets;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class AccountRepositoryTest {
    @Test public void registrationPersistsProtectedCredentialsAndPreservesAdmin() throws Exception {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        AccountRepository accounts = new AccountRepository(context);
        String username = "test_" + java.util.UUID.randomUUID();
        String key = "account_" + Base64.encodeToString(username.getBytes(StandardCharsets.UTF_8),
                Base64.NO_WRAP | Base64.URL_SAFE);
        SharedPreferences prefs = context.getSharedPreferences("registered_accounts", Context.MODE_PRIVATE);
        try {
            assertTrue(accounts.authenticate("admin", "123456".toCharArray()));
            assertFalse(accounts.authenticate("admin", "badpass".toCharArray()));
            assertEquals(AccountRepository.RegistrationResult.DUPLICATE,
                    accounts.register("Admin", "admin", "abcdef".toCharArray()));
            assertEquals(AccountRepository.RegistrationResult.SUCCESS,
                    accounts.register("Người kiểm thử", username, "secret123!".toCharArray()));
            assertEquals(AccountRepository.RegistrationResult.DUPLICATE,
                    accounts.register("Tên khác", username, "abcdef".toCharArray()));
            String record = prefs.getString(key, "");
            assertFalse(record.contains("secret123!"));
            org.json.JSONObject json = new org.json.JSONObject(record);
            assertTrue(json.has("salt"));
            assertTrue(json.has("hash"));
            assertTrue(json.has("iterations"));
            AccountRepository reopened = new AccountRepository(context);
            assertTrue(reopened.authenticate(username, "secret123!".toCharArray()));
            assertFalse(reopened.authenticate(username, "incorrect".toCharArray()));
            assertFalse(reopened.authenticate(username + "_missing", "secret123!".toCharArray()));
        } finally {
            assertTrue(prefs.edit().remove(key).commit());
        }
    }
}
