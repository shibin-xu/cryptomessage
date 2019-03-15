package crypto;

import org.bouncycastle.crypto.InvalidCipherTextException;
import org.junit.jupiter.api.Test;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FileEncryptTest extends CryptoTestBase {
    @Test
    public void smallFileTest() throws
            IOException,
            NoSuchAlgorithmException,
            InvalidCipherTextException,
            NoSuchProviderException,
            InvalidKeyException,
            NoSuchPaddingException,
            BadPaddingException,
            InvalidKeySpecException,
            IllegalBlockSizeException {
        fileTest("resources/small.txt", "out/small.encrypted.txt", "out/small.decrypted.txt");
    }

    @Test
    public void largeFileTest() throws
            IOException,
            NoSuchAlgorithmException,
            InvalidCipherTextException,
            NoSuchProviderException,
            InvalidKeyException,
            NoSuchPaddingException,
            BadPaddingException,
            InvalidKeySpecException,
            IllegalBlockSizeException {
        fileTest("resources/hamlet.txt", "out/hamlet.encrypted.txt", "out/hamlet.decrypted.txt");
    }

    private void fileTest(String f, String fEncrypted, String fDecrypted) throws
            IOException,
            NoSuchAlgorithmException,
            InvalidKeyException,
            InvalidKeySpecException,
            NoSuchPaddingException,
            BadPaddingException,
            IllegalBlockSizeException,
            NoSuchProviderException,
            InvalidCipherTextException {
        byte[] key = AESEncryptUtil.generateKey();

        File inf = new File(f);
        File outf = new File(fEncrypted);

        RandomAccessFile in = new RandomAccessFile(inf, "rw");
        RandomAccessFile out = new RandomAccessFile(outf, "rw");

        List<String> usernames = new ArrayList<String>();
        for (String each : accounts)
            usernames.add(each);
        FileEncryptUtil.encrypt(in, out, key, usernames);

        inf = new File(fEncrypted);
        outf = new File(fDecrypted);
        outf.createNewFile();

        in.close();
        out.close();

        for (String each : accounts) {
            in = new RandomAccessFile(inf, "rw");
            out = new RandomAccessFile(outf, "rw");

            FileEncryptUtil.decrypt(in, out, each, each);

            RandomAccessFile rafexpected = new RandomAccessFile(f, "r");
            RandomAccessFile rafactual = new RandomAccessFile(fDecrypted, "r");

            byte[] bytes = new byte[(int) rafexpected.length()];
            rafexpected.readFully(bytes);
            String expected = new String(bytes);

            bytes = new byte[(int) rafactual.length()];
            rafactual.readFully(bytes);
            String actual = new String(bytes);

            assertEquals(expected, actual);

            in.close();
            out.close();
            rafexpected.close();
            rafactual.close();
        }
    }
}
