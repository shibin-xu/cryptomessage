package org.cloudvault.commons;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DecryptAESResponseTest extends ServerTestBase {
    @Test
    public void serializationTest() {
        DecryptAESResponse expected = new DecryptAESResponse(true, "decryptedAESKey");
        String serialized = gson.toJson(expected);
        DecryptAESResponse actual = gson.fromJson(serialized, DecryptAESResponse.class);

        assertEquals(expected, actual);
    }
}
