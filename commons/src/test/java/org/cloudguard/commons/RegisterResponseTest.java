package org.cloudguard.commons;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RegisterResponseTest extends ServerTestBase {
    @Test
    public void serializationTest() {
        RegisterResponse expected = new RegisterResponse(true, "messagei");
        String serialized = gson.toJson(expected);
        RegisterResponse actual = gson.fromJson(serialized, RegisterResponse.class);

        assertEquals(expected, actual);
    }
}
