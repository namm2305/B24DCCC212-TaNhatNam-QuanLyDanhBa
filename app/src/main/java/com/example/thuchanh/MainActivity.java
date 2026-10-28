package com.example.thuchanh;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private ContactAdapter adapter;
    private final ActivityResultLauncher<Intent> contactResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> refresh());

    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        ScreenUi.setup(this, R.layout.activity_main);
        ListView list = findViewById(R.id.contacts);
        adapter = new ContactAdapter(this);
        list.setAdapter(adapter);
        list.setEmptyView(findViewById(R.id.empty));
        list.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(this, ContactDetailActivity.class);
            intent.putExtra(ContactDetailActivity.EXTRA_CONTACT_ID, adapter.getItem(position).getId());
            contactResult.launch(intent);
        });
        findViewById(R.id.addButton).setOnClickListener(v ->
                contactResult.launch(new Intent(this, AddContactActivity.class)));
        findViewById(R.id.logoutButton).setOnClickListener(v ->
                new androidx.appcompat.app.AlertDialog.Builder(this)
                        .setTitle(R.string.logout)
                        .setMessage(R.string.logout_confirmation)
                        .setNegativeButton(R.string.cancel_logout, null)
                        .setPositiveButton(R.string.logout, (dialog, which) -> {
                            // App không lưu phiên đăng nhập; giữ tài khoản và danh bạ trong RAM.
                            Intent intent = new Intent(this, LoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        })
                        .show());
    }
    @Override protected void onResume() { super.onResume(); refresh(); }
    private void refresh() {
        if (adapter == null) return;
        ContactRepository repository = ContactRepository.getInstance();
        adapter.submit(repository.getContacts());
        ((TextView) findViewById(R.id.total)).setText(String.valueOf(repository.count()));
        ((TextView) findViewById(R.id.favorites)).setText(String.valueOf(repository.countFavorites()));
    }
}
