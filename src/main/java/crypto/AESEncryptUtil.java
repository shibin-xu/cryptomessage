package crypto;

import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.engines.RijndaelEngine;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.paddings.BlockCipherPadding;
import org.bouncycastle.crypto.paddings.PKCS7Padding;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.security.SecureRandom;

public class AESEncryptUtil {
    /**
     * AES(file) format:
     *                   |--------------------------|      |-----------|
     *                   | AES encrypted block 0    | ===> | AES(file) |
     *                   | .                        |      |           |
     *                   | .                        |      |-----------|
     *                   | .                        |      | header    |
     *                   | AES encryption block n-1 |      |-----------|
     *                   |--------------------------|
     */

    protected static final String ALGORITHM = "AES-256";
    // key length = KEY_SIZE * 8 bits
    protected static final int KEY_SIZE = 32;

    private static final CBCBlockCipher cbcBlockCipher = new CBCBlockCipher(new RijndaelEngine(256));
    private static final SecureRandom PRNG = new SecureRandom();
    private static final BlockCipherPadding bcp = new PKCS7Padding();

    private AESEncryptUtil() {
        // Disable default constructor
    }

    /**
     * Generate AES-256 key
     *
     * @return key
     */
    public static byte[] generateKey() {
        byte[] key = new byte[KEY_SIZE];
        PRNG.nextBytes(key);
        return key;
    }

    /**
     * Encrypt or decrypt input by using AES-256 key
     *
     * @param input the original unencrypted text
     * @param encrypt true for encryption, and false for decryption
     * @param key the symmetric key
     * @return encryption/encryption result
     * @throws DataLengthException
     * @throws InvalidCipherTextException
     */
    private static byte[] processing(byte[] input, boolean encrypt, KeyParameter key) throws
            DataLengthException,
            InvalidCipherTextException {
        PaddedBufferedBlockCipher pbbc = new PaddedBufferedBlockCipher(cbcBlockCipher, bcp);

        int blockSize = cbcBlockCipher.getBlockSize();
        int inputOffset = 0;
        int inputLength = input.length;
        int outputOffset = 0;

        byte[] iv = new byte[blockSize];
        if(encrypt) {
            PRNG.nextBytes(iv);
            outputOffset += blockSize;
        } else {
            System.arraycopy(input, 0 , iv, 0, blockSize);
            inputOffset += blockSize;
            inputLength -= blockSize;
        }

        pbbc.init(encrypt, new ParametersWithIV(key, iv));
        byte[] output = new byte[pbbc.getOutputSize(inputLength) + outputOffset];
        if(encrypt)
            System.arraycopy(iv, 0 , output, 0, blockSize);
        int outputLength = outputOffset + pbbc.processBytes(input, inputOffset, inputLength, output, outputOffset);
        outputLength += pbbc.doFinal(output, outputLength);

        byte[] ret = new byte[outputLength];
        System.arraycopy(output, 0, ret, 0, outputLength);
        return ret;
    }

    /**
     * Encrypt a text with AES-256 key.
     *
     * @param text The original unencrypted text
     * @param key The AES-256 key
     * @return Encrypted text
     * @throws InvalidCipherTextException
     */
    public static byte[] encrypt(byte[] text, byte[] key) throws InvalidCipherTextException {
        return processing(text, true, new KeyParameter(key));
    }

    /**
     * Encrypt a text using AES-256  key. The result is encrypted BASE64 encoded text.
     *
     * @param text the original unencrypted text
     * @param key the AES-256 key
     * @return encrypted text encoded as BASE64
     * @throws UnsupportedEncodingException
     * @throws InvalidCipherTextException
     */
    public static String encrypt(String text, byte[]  key) throws
            UnsupportedEncodingException,
            InvalidCipherTextException {
        String encryptedText;
        byte[] cipherText = encrypt(text.getBytes("UTF8"),key);
        encryptedText = Base64.encodeBase64String((cipherText));
        return encryptedText;
    }

    /**
     * Decrypt text using AES-256  key
     *
     * @param text The encrypted text
     * @param key The AES-256  key
     * @return The unencrypted text
     * @throws InvalidCipherTextException
     */
    public static byte[] decrypt(byte[] text, byte[]  key) throws InvalidCipherTextException {
        return processing(text, false, new KeyParameter(key));
    }

    /**
     * Decrypt BASE64 encoded text using AES-256 key
     *
     * @param text the encrypted text, encoded as BASE64
     * @param key the AES-256 key
     * @return the unencrypted text encoded as UTF8
     * @throws IOException
     * @throws InvalidCipherTextException
     */
    public static String decrypt(String text, byte[] key) throws
            IOException,
            InvalidCipherTextException {
        return new String(decrypt(Base64.decodeBase64(text),key), "UTF8");
    }

    /**
     * Convert a Key to string encoded as BASE64
     *
     * @param key The key (private or public)
     * @return A string representation of the key
     */
    public static String getKeyAsString(Key key) {
        return Base64.encodeBase64String(key.getEncoded());
    }
}