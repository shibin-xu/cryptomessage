package org.cloudvault.commons;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DecryptAESRequestTest extends ServerTestBase {
    @Test
    public void serializationTest() {
        DecryptAESRequest expected = new DecryptAESRequest("token", "encryptedAESKey");
        String serialized = gson.toJson(expected);
        DecryptAESRequest actual = gson.fromJson(serialized, DecryptAESRequest.class);

        assertEquals(expected, actual);
    }
}
