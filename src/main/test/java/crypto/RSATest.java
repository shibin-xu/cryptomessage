package crypto;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.RandomAccessFile;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class RSATest extends CryptoTestBase {
    @Test
    public void smallStringTest() throws Exception {
        KeyPair keyPair = RSAEncryptUtil.generateKey();
        String expected = "smallStringTest";
        PublicKey pub = keyPair.getPublic();
        PrivateKey pri = keyPair.getPrivate();
        String encrypted = RSAEncryptUtil.encrypt(expected, pub);
        String actual = RSAEncryptUtil.decrypt(encrypted, pri);

        assertEquals(expected, actual);
    }

    @Test
    public void largeStringTest() throws Exception {
        KeyPair keyPair = RSAEncryptUtil.generateKey();
        // RSA is able to encrypt plaintext as large as key size
        String expected = "";
        for (int i = 0; i < RSAEncryptUtil.getKeySize()/(8*2); i++)
            expected += i % 10;
        PublicKey pub = keyPair.getPublic();
        PrivateKey pri = keyPair.getPrivate();
        String encrypted = RSAEncryptUtil.encrypt(expected, pub);
        String actual = RSAEncryptUtil.decrypt(encrypted, pri);

        assertEquals(expected, actual);
    }

    @Test
    public void readKeyTest() throws Exception {
        KeyPair keyPair = RSAEncryptUtil.generateKey();
        PublicKey pub = keyPair.getPublic();
        PrivateKey pri = keyPair.getPrivate();

        String pubstr = RSAEncryptUtil.getKeyAsString(pub);
        String pristr = RSAEncryptUtil.getKeyAsString(pri);

        File pubf = new File("out/RSATest.readKeyTest.pub");
        File prif = new File("out/RSATest.readKeyTest.pri");

        RandomAccessFile pubraf = new RandomAccessFile(pubf, "rw");
        RandomAccessFile priraf = new RandomAccessFile(prif, "rw");

        pubraf.writeUTF(pubstr);
        priraf.writeUTF(pristr);

        pubraf.seek(0L);
        priraf.seek(0L);

        String pubstrActual = pubraf.readUTF();
        String pristrActual = priraf.readUTF();

        PublicKey pubActual = RSAEncryptUtil.getPublicKeyFromString(pubstrActual);
        PrivateKey priActual = RSAEncryptUtil.getPrivateKeyFromString(pristrActual);

        assertArrayEquals(pub.getEncoded(), pubActual.getEncoded());
        assertArrayEquals(pri.getEncoded(), priActual.getEncoded());

        pubraf.close();
        priraf.close();
    }
}
