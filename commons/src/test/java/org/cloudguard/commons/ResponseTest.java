package org.cloudguard.commons;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ResponseTest extends ServerTestBase {
    @Test
    public void serializationTest() throws Exception {
        LoginPrepareResponse expected = new LoginPrepareResponse( "");
        String serialized = gson.toJson(expected);

        Response request = gson.fromJson(gson.toJson(new Response(expected.getClass().getName(), serialized)), Response.class);
        LoginPrepareResponse actual = (LoginPrepareResponse)gson.fromJson(request.getJson(), Class.forName(request.getClassName()));

        assertEquals(expected, actual);
    }
}
