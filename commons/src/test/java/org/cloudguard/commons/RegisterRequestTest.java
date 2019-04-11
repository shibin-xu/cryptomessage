package org.cloudguard.commons;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RegisterRequestTest extends ServerTestBase {
    @Test
    public void serializationTest() {
        RegisterRequest expected = new RegisterRequest("email", "firstName", "lastName", "password");
        String serialized = gson.toJson(expected);
        RegisterRequest actual = gson.fromJson(serialized, RegisterRequest.class);

        assertEquals(expected, actual);
    }
}
