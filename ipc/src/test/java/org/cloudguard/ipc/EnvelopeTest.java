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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    @Test
    public void chainTest() throws Exception {
        String body = "This is Message " + 0;
        String hashOfLastMessage = PasswordUtil.hash("");
        Date date = new Date();
        Message message = new Message(body, hashOfLastMessage, date.getTime());
        String text = gson.toJson(message);
        String proof = RSAEncryptUtil.sign(text, privateKey);
        Envelope envelope = new Envelope(message, proof);
        List<Envelope> list = new ArrayList<>();
        list.add(envelope);

        // Construct a chain of messages
        for (int i = 1; i < 10; i++) {
            body = "This is Message " + i;
            hashOfLastMessage = PasswordUtil.hash(text);
            date = new Date();
            message = new Message(body, hashOfLastMessage, date.getTime());
            text = gson.toJson(message);
            proof = RSAEncryptUtil.sign(text, privateKey);
            envelope = new Envelope(message, proof);
            list.add(envelope);
        }

        // Verify the chain
        boolean expected = true;
        envelope = list.get(0);
        message = envelope.getMessage();
        proof = envelope.getSignature();
        assertEquals(message.getHashOfLastMessage(), PasswordUtil.hash(""));
        assertEquals(true, RSAEncryptUtil.verify(gson.toJson(message), proof, publicKey));

        for (int i = 1; i < 10; i++) {
            hashOfLastMessage = PasswordUtil.hash(gson.toJson(message));

            envelope = list.get(i);
            message = envelope.getMessage();
            proof = envelope.getSignature();
            assertEquals(message.getHashOfLastMessage(), hashOfLastMessage);
            assertEquals(true, RSAEncryptUtil.verify(gson.toJson(message), proof, publicKey));
        }
    }
}
