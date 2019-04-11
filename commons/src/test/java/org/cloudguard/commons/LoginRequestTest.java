package org.cloudvault.commons;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LoginRequestTest extends ServerTestBase {
    @Test
    public void serializationTest() {
        LoginRequest expected = new LoginRequest("email", "passowrd");
        String serialized = gson.toJson(expected);
        LoginRequest actual = gson.fromJson(serialized, LoginRequest.class);

        assertEquals(expected, actual);
    }
}
