package com.example.thuchanh;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/** Băm chậm với salt ngẫu nhiên; không lưu hoặc mã hóa mật khẩu để giải mã lại. */
public final class PasswordHasher {
    private PasswordHasher() {}
    public static byte[] newSalt() {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        return salt;
    }
    public static byte[] hash(char[] password, byte[] salt, String algorithm, int iterations)
            throws GeneralSecurityException {
        PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, 256);
        try { return SecretKeyFactory.getInstance(algorithm).generateSecret(spec).getEncoded(); }
        finally { spec.clearPassword(); }
    }
    public static boolean verify(char[] password, byte[] salt, String algorithm, int iterations, byte[] expected)
            throws GeneralSecurityException {
        byte[] actual = hash(password, salt, algorithm, iterations);
        try { return MessageDigest.isEqual(expected, actual); }
        finally { Arrays.fill(actual, (byte) 0); }
    }
}
