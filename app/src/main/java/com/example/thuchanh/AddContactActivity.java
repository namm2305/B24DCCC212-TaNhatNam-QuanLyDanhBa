package com.example.thuchanh;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class AddContactActivity extends AppCompatActivity {
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        ScreenUi.setup(this, R.layout.activity_add_contact);
        EditText name = findViewById(R.id.fullName), phone = findViewById(R.id.phone), note = findViewById(R.id.note);
        Spinner group = findViewById(R.id.group);
        ArrayAdapter<CharSequence> groups = ArrayAdapter.createFromResource(this, R.array.contact_groups, android.R.layout.simple_spinner_item);
        groups.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        group.setAdapter(groups);
        RadioGroup gender = findViewById(R.id.gender);
        CheckBox favorite = findViewById(R.id.favorite);
        findViewById(R.id.backButton).setOnClickListener(v -> finish());
        findViewById(R.id.saveButton).setOnClickListener(v -> {
            String fullName = name.getText().toString().trim();
            String number = phone.getText().toString().trim();
            name.setError(null); phone.setError(null);
            if (fullName.isEmpty()) {
                name.setError("Họ tên không được bỏ trống"); name.requestFocus(); return;
            }
            if (!number.matches("0[0-9]{9}")) {
                phone.setError("Số điện thoại phải gồm đúng 10 chữ số và bắt đầu bằng 0");
                phone.requestFocus(); return;
            }
            ContactRepository repository = ContactRepository.getInstance();
            if (repository.hasPhoneNumber(number)) {
                phone.setError("Số điện thoại đã có trong danh bạ"); phone.requestFocus(); return;
            }
            RadioButton selected = findViewById(gender.getCheckedRadioButtonId());
            Contact contact = new Contact(fullName, number, group.getSelectedItem().toString(),
                    selected.getText().toString(), favorite.isChecked(), note.getText().toString().trim());
            if (repository.addContact(contact)) {
                Toast.makeText(this, "Đã thêm liên hệ", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            }
        });
    }
}
