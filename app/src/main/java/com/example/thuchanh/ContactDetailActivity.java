package com.example.thuchanh;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class ContactDetailActivity extends AppCompatActivity {
    public static final String EXTRA_CONTACT_ID = "contact_id";
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        ScreenUi.setup(this, R.layout.activity_contact_detail);
        ContactRepository repository = ContactRepository.getInstance();
        Contact contact = repository.findById(getIntent().getStringExtra(EXTRA_CONTACT_ID));
        // ID cũ không còn hợp lệ sau khi tiến trình bị hệ thống hủy.
        if (contact == null) {
            Toast.makeText(this, "Liên hệ không còn tồn tại trong phiên hiện tại", Toast.LENGTH_LONG).show();
            finish(); return;
        }
        ScreenUi.avatar(findViewById(R.id.avatar), contact);
        ((TextView) findViewById(R.id.contactName)).setText(contact.getFullName());
        ((TextView) findViewById(R.id.contactPhone)).setText(contact.getPhoneNumber());
        ((TextView) findViewById(R.id.contactGroup)).setText(contact.getGroup());
        ((TextView) findViewById(R.id.contactGender)).setText(contact.getGender());
        ((TextView) findViewById(R.id.contactNote)).setText(contact.getNote().isEmpty() ? "Chưa có ghi chú" : contact.getNote());
        CheckBox favorite = findViewById(R.id.favorite);
        favorite.setChecked(contact.isFavorite());
        favorite.setOnCheckedChangeListener((button, checked) -> {
            repository.setFavorite(contact.getId(), checked);
            setResult(RESULT_OK);
        });
        findViewById(R.id.backButton).setOnClickListener(v -> finish());
        findViewById(R.id.callButton).setOnClickListener(v -> {
            Intent dial = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", contact.getPhoneNumber(), null));
            if (dial.resolveActivity(getPackageManager()) == null) {
                Toast.makeText(this, "Không tìm thấy ứng dụng quay số", Toast.LENGTH_SHORT).show(); return;
            }
            try { startActivity(dial); }
            catch (ActivityNotFoundException e) {
                Toast.makeText(this, "Không thể mở ứng dụng quay số", Toast.LENGTH_SHORT).show();
            }
        });
        findViewById(R.id.deleteButton).setOnClickListener(v ->
                new AlertDialog.Builder(this).setTitle("Xóa liên hệ?")
                        .setMessage("Bạn có muốn xóa " + contact.getFullName() + " khỏi danh bạ?")
                        .setNegativeButton("Hủy", null)
                        .setPositiveButton("Xóa", (dialog, which) -> {
                            repository.deleteById(contact.getId());
                            setResult(RESULT_OK);
                            Toast.makeText(this, "Đã xóa liên hệ", Toast.LENGTH_SHORT).show();
                            finish();
                        }).show());
    }
}
