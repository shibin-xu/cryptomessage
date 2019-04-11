package org.cloudguard.commons;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PublicKeyResponseTest extends ServerTestBase {
    @Test
    public void serializationTest() {
        PublicKeyResponse expected = new PublicKeyResponse(true, new HashMap<String, String>());
        String serialized = gson.toJson(expected);
        PublicKeyResponse actual = gson.fromJson(serialized, PublicKeyResponse.class);

        assertEquals(expected, actual);
    }
}
