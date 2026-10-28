package com.example.thuchanh;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private EditText username, password;
    private boolean busy;
    private final ActivityResultLauncher<Intent> registerResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    if (result.getData() != null) username.setText(result.getData().getStringExtra("username"));
                    password.setText("");
                    Toast.makeText(this, R.string.registration_success, Toast.LENGTH_LONG).show();
                }
            });
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        ScreenUi.setup(this, R.layout.activity_login);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        TextView error = findViewById(R.id.loginError);
        findViewById(R.id.registerLink).setOnClickListener(v ->
                registerResult.launch(new Intent(this, RegisterActivity.class)));
        findViewById(R.id.loginButton).setOnClickListener(v -> {
            if (busy) return;
            error.setText("");
            String name = username.getText().toString().trim();
            if (name.isEmpty()) {
                username.setError("Vui lòng nhập tên đăng nhập"); username.requestFocus(); return;
            }
            if (password.getText().toString().isEmpty()) {
                password.setError("Vui lòng nhập mật khẩu"); password.requestFocus(); return;
            }
            char[] secret = password.getText().toString().toCharArray();
            setBusy(true);
            executor.execute(() -> {
                boolean valid = false;
                boolean failed = false;
                try { valid = new AccountRepository(this).authenticate(name, secret); }
                catch (java.security.GeneralSecurityException e) { failed = true; }
                finally { Arrays.fill(secret, '\0'); }
                final boolean success = valid, unavailable = failed;
                runOnUiThread(() -> {
                    if (isFinishing() || isDestroyed()) return;
                    setBusy(false);
                    if (unavailable) { error.setText(R.string.auth_unavailable); return; }
                    if (!success) { error.setText("Sai tên đăng nhập hoặc mật khẩu"); return; }
                    password.setText("");
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                });
            });
        });
    }
    private void setBusy(boolean value) {
        busy = value;
        username.setEnabled(!value); password.setEnabled(!value);
        findViewById(R.id.loginButton).setEnabled(!value);
        findViewById(R.id.registerLink).setEnabled(!value);
        ((TextView) findViewById(R.id.loginButton)).setText(value ? R.string.auth_processing : R.string.ui_8);
    }
    @Override protected void onDestroy() { executor.shutdown(); super.onDestroy(); }
}
