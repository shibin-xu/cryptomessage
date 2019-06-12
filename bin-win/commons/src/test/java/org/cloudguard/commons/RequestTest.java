package org.cloudguard.commons;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RequestTest extends ServerTestBase {
    @Test
    public void serializationTest() throws Exception {
        LoginPrepareRequest expected = new LoginPrepareRequest("", "");
        String serialized = gson.toJson(expected);

        Request request = gson.fromJson(gson.toJson(new Request(expected.getClass().getName(), serialized)), Request.class);
        LoginPrepareRequest actual = (LoginPrepareRequest)gson.fromJson(request.getJson(), Class.forName(request.getClassName()));

        assertEquals(expected, actual);
    }
}
