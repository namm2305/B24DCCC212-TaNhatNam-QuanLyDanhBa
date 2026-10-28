package com.example.thuchanh;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Base64;
import org.json.JSONException;
import org.json.JSONObject;
import java.security.GeneralSecurityException;
import java.util.Arrays;

public final class AccountRepository {
    private static final Object LOCK = new Object();
    private final SharedPreferences preferences;
    public enum RegistrationResult { SUCCESS, DUPLICATE, STORAGE_ERROR }
    public AccountRepository(Context context) {
        preferences = context.getApplicationContext().getSharedPreferences("registered_accounts", Context.MODE_PRIVATE);
    }
    private String key(String username) {
        return "account_" + Base64.encodeToString(username.trim().getBytes(java.nio.charset.StandardCharsets.UTF_8),
                Base64.NO_WRAP | Base64.URL_SAFE);
    }
    public boolean usernameExists(String username) {
        return "admin".equals(username.trim()) || preferences.contains(key(username));
    }
    // Chạy trên luồng nền: PBKDF2 tốn CPU, commit cần đợi kết quả lưu thật sự.
    public RegistrationResult register(String fullName, String username, char[] password)
            throws GeneralSecurityException {
        synchronized (LOCK) {
            if (usernameExists(username)) return RegistrationResult.DUPLICATE;
            String algorithm = Build.VERSION.SDK_INT >= 26 ? "PBKDF2WithHmacSHA256" : "PBKDF2WithHmacSHA1";
            int iterations = Build.VERSION.SDK_INT >= 26 ? 600000 : 1300000;
            byte[] salt = PasswordHasher.newSalt();
            byte[] hash = PasswordHasher.hash(password, salt, algorithm, iterations);
            try {
                JSONObject record = new JSONObject();
                record.put("fullName", fullName.trim());
                record.put("salt", Base64.encodeToString(salt, Base64.NO_WRAP));
                record.put("hash", Base64.encodeToString(hash, Base64.NO_WRAP));
                record.put("algorithm", algorithm);
                record.put("iterations", iterations);
                return preferences.edit().putString(key(username), record.toString()).commit()
                        ? RegistrationResult.SUCCESS : RegistrationResult.STORAGE_ERROR;
            } catch (JSONException e) {
                return RegistrationResult.STORAGE_ERROR;
            } finally { Arrays.fill(hash, (byte) 0); }
        }
    }
    public boolean authenticate(String username, char[] password) throws GeneralSecurityException {
        if ("admin".equals(username.trim())) {
            return java.security.MessageDigest.isEqual(
                    "123456".getBytes(java.nio.charset.StandardCharsets.UTF_8),
                    new String(password).getBytes(java.nio.charset.StandardCharsets.UTF_8));
        }
        String encoded = preferences.getString(key(username), null);
        if (encoded == null) return false;
        try {
            JSONObject record = new JSONObject(encoded);
            String algorithm = record.getString("algorithm");
            int iterations = record.getInt("iterations");
            if (!(algorithm.equals("PBKDF2WithHmacSHA256") && iterations == 600000)
                    && !(algorithm.equals("PBKDF2WithHmacSHA1") && iterations == 1300000)) return false;
            byte[] salt = Base64.decode(record.getString("salt"), Base64.NO_WRAP);
            byte[] hash = Base64.decode(record.getString("hash"), Base64.NO_WRAP);
            if (salt.length != 16 || hash.length != 32) return false;
            return PasswordHasher.verify(password, salt, algorithm, iterations, hash);
        } catch (JSONException | IllegalArgumentException e) { return false; }
    }
}
