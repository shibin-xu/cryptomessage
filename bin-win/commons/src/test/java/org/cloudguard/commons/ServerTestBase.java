package org.cloudguard.commons;

import com.google.gson.Gson;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

public class ServerTestBase {
    protected static Gson gson;

    @BeforeAll
    protected static void setup() {
        gson = new Gson();
    }

    @AfterAll
    protected static void teardown() {
        // TBD
    }
}
