package crypto;

import org.apache.commons.codec.binary.Base64;
import org.junit.jupiter.api.Test;

import java.security.KeyPair;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class NameKeyTest extends CryptoTestBase {
    @Test
    public void smallStringTest() throws Exception {
        stringTest("smallStringTest",  "username");
    }

    @Test
    public void largeStringTest() throws Exception {
        byte[] bytes = new byte[102400];
        PRNG.nextBytes(bytes);
        String f = new String(bytes);
        bytes = new byte[102400];
        PRNG.nextBytes(bytes);
        String username = new String(bytes);

        stringTest(f, username);
    }

    private void stringTest(String f, String username) throws Exception {
        byte[] keyAES = AESEncryptUtil.generateKey();
        KeyPair keyPair = RSAEncryptUtil.generateKey();
        byte[] name = PasswordUtil.hash(NameKey.xor(PasswordUtil.hash(f.getBytes()),
                PasswordUtil.hash(username.getBytes())));
        byte[] key = RSAEncryptUtil.encrypt(keyAES, keyPair.getPublic());

        NameKey nameKey = new NameKey(name, key);
        assertArrayEquals(name, nameKey.getName());
        assertArrayEquals(keyAES, RSAEncryptUtil.decrypt(nameKey.getKey(), keyPair.getPrivate()));
        assertEquals(Base64.encodeBase64String(name), nameKey.getNameBase64());
        assertEquals(Base64.encodeBase64String(key), nameKey.getKeyBase64());

        nameKey = new NameKey(Base64.encodeBase64String(name), Base64.encodeBase64String(key));
        assertArrayEquals(name, nameKey.getName());
        assertArrayEquals(keyAES, RSAEncryptUtil.decrypt(nameKey.getKey(), keyPair.getPrivate()));
        assertEquals(Base64.encodeBase64String(name), nameKey.getNameBase64());
        assertEquals(Base64.encodeBase64String(key), nameKey.getKeyBase64());
    }
}
