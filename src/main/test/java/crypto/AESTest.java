package crypto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AESTest extends CryptoTestBase {
    @Test
    public void smallStringTest() throws Exception {
        byte[] key = AESEncryptUtil.generateKey();
        String expected = "smallStringTest";
        String encrypted = AESEncryptUtil.encrypt(expected, key);
        String actual = AESEncryptUtil.decrypt(encrypted, key);

        assertEquals(expected, actual);
    }

    @Test
    public void largeStringTest() throws Exception {
        byte[] key = AESEncryptUtil.generateKey();
        byte[] feed = new byte[10240];
        PRNG.nextBytes(feed);
        String expected = new String(feed);
        String encrypted = AESEncryptUtil.encrypt(expected, key);
        String actual = AESEncryptUtil.decrypt(encrypted, key);

        assertEquals(expected, actual);
    }
}
