package org.cloudguard.commons;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LoginResponseTest extends ServerTestBase {
    @Test
    public void serializationTest() {
        LoginResponse expected = new LoginResponse(true, "token", "message");
        String serialized = gson.toJson(expected);
        LoginResponse actual = gson.fromJson(serialized, LoginResponse.class);

        assertEquals(expected, actual);
    }
}
