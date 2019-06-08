package org.cloudguard.crypto;

import com.google.gson.Gson;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RSAEncryptUtilTest {
    protected static Gson gson;
    protected static KeyPair keyPair;
    protected static PublicKey publicKey;
    protected static PrivateKey privateKey;

    @BeforeAll
    protected static void setup() throws
            NoSuchAlgorithmException {
        gson = new Gson();
        CryptoUtil.init();
        keyPair = RSAEncryptUtil.generateKey();
        publicKey = keyPair.getPublic();
        privateKey = keyPair.getPrivate();
    }

    @AfterAll
    protected static void teardown() {
        // TBD
    }

    @Test
    public void signVerifyTest() throws Exception {
        String text = "This is a test.";
        String proof = RSAEncryptUtil.sign(text, privateKey);
        boolean expected = true;
        boolean actual = RSAEncryptUtil.verify(text, proof, publicKey);

        assertEquals(expected, actual);
    }
}
