package org.cloudguard.ipc;

import com.google.gson.Gson;
import org.cloudguard.commons.Message;
import org.cloudguard.crypto.CryptoUtil;
import org.cloudguard.crypto.PasswordUtil;
import org.cloudguard.crypto.RSAEncryptUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.security.*;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MessageTest {
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
            NoSuchAlgorithmException {
        String body = "This is a test.";
        String hashOfLastMessage = PasswordUtil.hash("");
        Date date = new Date();
        Message expected = new Message(body, "", hashOfLastMessage, date.getTime());
        String serialized = gson.toJson(expected);
        Message actual = gson.fromJson(serialized, Message.class);

        assertEquals(expected, actual);
    }
}
