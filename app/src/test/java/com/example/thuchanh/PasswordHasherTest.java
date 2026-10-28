package com.example.thuchanh;

import org.junit.Test;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import static org.junit.Assert.*;

public class PasswordHasherTest {
    @Test public void matchesKnownPbkdf2Sha256Vector() throws Exception {
        byte[] hash = PasswordHasher.hash("password".toCharArray(),
                "salt".getBytes(StandardCharsets.UTF_8), "PBKDF2WithHmacSHA256", 1);
        StringBuilder hex = new StringBuilder();
        for (byte value : hash) hex.append(String.format("%02x", value & 255));
        assertEquals("120fb6cffcf8b32c43e7225256c4f837a86548c92ccc35480805987cb70be17b", hex.toString());
    }
    @Test public void verifiesOnlyCorrectPasswordAndSalt() throws Exception {
        byte[] salt = PasswordHasher.newSalt();
        char[] password = "Mat khau Việt 123".toCharArray();
        byte[] expected = PasswordHasher.hash(password, salt, "PBKDF2WithHmacSHA256", 1000);
        assertTrue(PasswordHasher.verify(password, salt, "PBKDF2WithHmacSHA256", 1000, expected));
        assertFalse(PasswordHasher.verify("wrong".toCharArray(), salt, "PBKDF2WithHmacSHA256", 1000, expected));
        assertFalse(PasswordHasher.verify(password, PasswordHasher.newSalt(), "PBKDF2WithHmacSHA256", 1000, expected));
        assertEquals(32, expected.length);
        assertArrayEquals("Mat khau Việt 123".toCharArray(), password);
    }
    @Test public void saltsAreRandomAndLegacyAlgorithmWorks() throws Exception {
        byte[] salt = PasswordHasher.newSalt();
        assertEquals(16, salt.length);
        assertFalse(Arrays.equals(salt, PasswordHasher.newSalt()));
        byte[] expected = PasswordHasher.hash("123456".toCharArray(), salt, "PBKDF2WithHmacSHA1", 1000);
        assertTrue(PasswordHasher.verify("123456".toCharArray(), salt, "PBKDF2WithHmacSHA1", 1000, expected));
    }
}
