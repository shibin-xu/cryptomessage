package org.cloudvault.commons;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PublicKeyRequestTest extends ServerTestBase {
    @Test
    public void serializationTest() {
        PublicKeyRequest expected = new PublicKeyRequest("token", new ArrayList<String>());
        String serialized = gson.toJson(expected);
        PublicKeyRequest actual = gson.fromJson(serialized, PublicKeyRequest.class);

        assertEquals(expected, actual);
    }
}
