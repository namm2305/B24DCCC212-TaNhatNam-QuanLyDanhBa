package com.example.thuchanh;

import java.util.ArrayList;

public final class ContactRepository {
    private static final ContactRepository INSTANCE = new ContactRepository();
    private final ArrayList<Contact> contacts = new ArrayList<>();

    // Singleton khởi tạo mẫu đúng một lần trong mỗi tiến trình.
    private ContactRepository() {
        contacts.add(new Contact("Nguyễn Văn An", "0901234567", "Bạn bè", "Nam", false, ""));
        contacts.add(new Contact("Trần Thị Bình", "0912345678", "Gia đình", "Nữ", true, ""));
        contacts.add(new Contact("Lê Minh Châu", "0987654321", "Công việc", "Khác", false, ""));
    }
    public static ContactRepository getInstance() { return INSTANCE; }
    public ArrayList<Contact> getContacts() { return new ArrayList<>(contacts); }
    public boolean hasPhoneNumber(String phone) {
        for (Contact c : contacts) if (c.getPhoneNumber().equals(phone)) return true;
        return false;
    }
    public boolean addContact(Contact contact) {
        if (hasPhoneNumber(contact.getPhoneNumber())) return false;
        contacts.add(contact); return true;
    }
    public Contact findById(String id) {
        for (Contact c : contacts) if (c.getId().equals(id)) return c;
        return null;
    }
    public boolean deleteById(String id) {
        Contact c = findById(id);
        return c != null && contacts.remove(c);
    }
    public void setFavorite(String id, boolean favorite) {
        Contact c = findById(id);
        if (c != null) c.setFavorite(favorite);
    }
    public int count() { return contacts.size(); }
    public int countFavorites() {
        int count = 0;
        for (Contact c : contacts) if (c.isFavorite()) count++;
        return count;
    }
}
