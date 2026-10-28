package com.example.thuchanh;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RegisterActivity extends AppCompatActivity {
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private EditText fullName, username, password, confirmation;
    private boolean busy;
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        ScreenUi.setup(this, R.layout.activity_register);
        fullName = findViewById(R.id.registerFullName);
        username = findViewById(R.id.registerUsername);
        password = findViewById(R.id.registerPassword);
        confirmation = findViewById(R.id.confirmPassword);
        findViewById(R.id.backToLogin).setOnClickListener(v -> finish());
        findViewById(R.id.registerButton).setOnClickListener(v -> register());
    }
    private boolean required(EditText field, int message) {
        if (!field.getText().toString().trim().isEmpty()) return true;
        field.setError(getString(message)); field.requestFocus(); return false;
    }
    private void register() {
        if (busy) return;
        fullName.setError(null); username.setError(null); password.setError(null); confirmation.setError(null);
        if (!required(fullName, R.string.name_required) || !required(username, R.string.username_required)
                || !required(password, R.string.password_required) || !required(confirmation, R.string.confirm_required)) return;
        if (password.length() < 6) {
            password.setError(getString(R.string.password_short)); password.requestFocus(); return;
        }
        if (!password.getText().toString().equals(confirmation.getText().toString())) {
            confirmation.setError(getString(R.string.password_mismatch)); confirmation.requestFocus(); return;
        }
        String name = fullName.getText().toString().trim(), user = username.getText().toString().trim();
        AccountRepository accounts = new AccountRepository(this);
        if (accounts.usernameExists(user)) {
            username.setError(getString(R.string.username_duplicate)); username.requestFocus(); return;
        }
        char[] secret = password.getText().toString().toCharArray();
        setBusy(true);
        executor.execute(() -> {
            AccountRepository.RegistrationResult result;
            try { result = accounts.register(name, user, secret); }
            catch (java.security.GeneralSecurityException e) { result = AccountRepository.RegistrationResult.STORAGE_ERROR; }
            finally { Arrays.fill(secret, '\0'); }
            final AccountRepository.RegistrationResult outcome = result;
            runOnUiThread(() -> {
                if (isFinishing() || isDestroyed()) return;
                setBusy(false);
                if (outcome == AccountRepository.RegistrationResult.DUPLICATE) {
                    username.setError(getString(R.string.username_duplicate)); username.requestFocus();
                } else if (outcome == AccountRepository.RegistrationResult.STORAGE_ERROR) {
                    Toast.makeText(this, R.string.auth_unavailable, Toast.LENGTH_LONG).show();
                } else {
                    password.setText(""); confirmation.setText("");
                    setResult(RESULT_OK, new Intent().putExtra("username", user));
                    finish();
                }
            });
        });
    }
    private void setBusy(boolean value) {
        busy = value;
        fullName.setEnabled(!value); username.setEnabled(!value);
        password.setEnabled(!value); confirmation.setEnabled(!value);
        findViewById(R.id.registerButton).setEnabled(!value);
        findViewById(R.id.backToLogin).setEnabled(!value);
        ((TextView) findViewById(R.id.registerButton)).setText(value ? R.string.auth_processing : R.string.register_title);
    }
    @Override protected void onDestroy() { executor.shutdown(); super.onDestroy(); }
}
