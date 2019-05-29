package org.cloudguard.ipc;

import com.google.gson.Gson;
import org.cloudguard.crypto.CryptoUtil;
import org.cloudguard.crypto.PasswordUtil;
import org.cloudguard.crypto.RSAEncryptUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.UnsupportedEncodingException;
import java.security.*;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EnvelopeTest {
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
    public void serializationTest() throws
            NoSuchProviderException,
            NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeyException, SignatureException {
        String body = "This is a test.";
        String hashOfLastMessage = PasswordUtil.hash("");
        Date date = new Date();
        Message message = new Message(body, hashOfLastMessage, date.getTime());
        String signature = RSAEncryptUtil.sign(gson.toJson(message), privateKey);
        Envelope expected = new Envelope(message, signature);
        String serialized = gson.toJson(expected);
        Envelope actual = gson.fromJson(serialized, Envelope.class);

        assertEquals(expected, actual);
    }

    @Test
    public void signVerifyTest() throws Exception {
        String body = "This is a test.";
        String hashOfLastMessage = PasswordUtil.hash("");
        Date date = new Date();
        Message message = new Message(body, hashOfLastMessage, date.getTime());
        String text = gson.toJson(message);
        String proof = RSAEncryptUtil.sign(text, privateKey);
        boolean expected = true;
        boolean actual = RSAEncryptUtil.verify(gson.toJson(message), proof, publicKey);

        assertEquals(expected, actual);
    }
}
