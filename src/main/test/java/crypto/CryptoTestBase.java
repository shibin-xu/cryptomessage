package crypto;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import ssl.ClientUtil;
import ssl.ServerResponse;

import java.io.File;
import java.io.IOException;
import java.rmi.ServerException;
import java.security.SecureRandom;

public class CryptoTestBase {
    protected static final SecureRandom PRNG = new SecureRandom();
    protected static final String[] accounts = new String[] {
            "test.crypto.0",
            "test.crypto.1",
            "test.crypto.2",
            "test.crypto.3",
            "test.crypto.4",
            "test.crypto.5",
            "test.crypto.6",
            "test.crypto.7",
            "test.crypto.8",
            "test.crypto.9"
    };

    @BeforeAll
    protected static void setup() throws ServerException {
        CryptoUtil.init();
        File outDir = new File("out");
        if (!outDir.exists())
            outDir.mkdir();

        ServerResponse response;
        for (String each : accounts) {
            response = ClientUtil.register(each, each);
            if (!response.isGood()) {
                response = ClientUtil.login(each, each);
                if (!response.isGood())
                    throw new ServerException("CryptoTestBase.setup() unable to create test account = " + each);
            }
        }
    }

    @AfterAll
    protected static void teardown() throws IOException {
        File outDir = new File("out");
        if (outDir.exists()) {
            FileUtils.deleteDirectory(outDir);
        }
    }
}
