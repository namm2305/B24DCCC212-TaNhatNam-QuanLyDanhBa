package com.example.thuchanh;

import org.junit.Test;
import static org.junit.Assert.*;

public class ContactRepositoryTest {
    @Test public void repositoryMaintainsIdentityCountsAndUniquePhones() {
        ContactRepository r = ContactRepository.getInstance();
        assertSame(r, ContactRepository.getInstance());
        assertEquals(3, r.count());
        assertEquals(1, r.countFavorites());
        assertEquals("Nguyễn Văn An", r.getContacts().get(0).getFullName());
        Contact c = new Contact("  Nguyễn Văn Đức  ", "0900000000", "Khác", "Nam", false, "Ghi chú");
        assertEquals("Đ", c.getInitial());
        assertTrue(r.addContact(c));
        assertEquals(4, r.count());
        assertSame(c, r.findById(c.getId()));
        assertFalse(r.addContact(new Contact("Trùng", "0900000000", "Khác", "Nữ", true, "")));
        r.setFavorite(c.getId(), true);
        assertEquals(2, r.countFavorites());
        r.getContacts().clear();
        assertEquals(4, r.count());
        assertTrue(r.deleteById(c.getId()));
        assertNull(r.findById(c.getId()));
        assertFalse(r.deleteById(c.getId()));
        assertEquals(3, r.count());
        assertEquals(1, r.countFavorites());
    }
}
