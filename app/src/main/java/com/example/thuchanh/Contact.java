package com.example.thuchanh;

import java.util.Locale;
import java.util.UUID;

public class Contact {
    private final String id;
    private String fullName, phoneNumber, group, gender, note;
    private boolean favorite;

    public Contact(String fullName, String phoneNumber, String group, String gender, boolean favorite, String note) {
        this.id = UUID.randomUUID().toString();
        this.fullName = fullName; this.phoneNumber = phoneNumber; this.group = group;
        this.gender = gender; this.favorite = favorite; this.note = note;
    }
    public String getId() { return id; }
    public String getFullName() { return fullName; }
    public void setFullName(String value) { fullName = value; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String value) { phoneNumber = value; }
    public String getGroup() { return group; }
    public void setGroup(String value) { group = value; }
    public String getGender() { return gender; }
    public void setGender(String value) { gender = value; }
    public boolean isFavorite() { return favorite; }
    public void setFavorite(boolean value) { favorite = value; }
    public String getNote() { return note; }
    public void setNote(String value) { note = value; }
    public String getInitial() {
        String name = fullName == null ? "" : fullName.trim();
        if (name.isEmpty()) return "?";
        String[] words = name.split("\\s+");
        String last = words[words.length - 1];
        return last.substring(0, last.offsetByCodePoints(0, 1)).toUpperCase(new Locale("vi"));
    }
}
