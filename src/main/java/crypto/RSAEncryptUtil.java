package crypto;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class RSAEncryptUtil {
    // Encryption result length in bytes
    public static final int RESULT_SIZE = 256;

    protected static final String ALGORITHM = "RSA";
    protected static final int KEY_SIZE = 2048;

    private RSAEncryptUtil() {
        // Disable default constructor
    }

    /**
     * Generate key which contains a pair of privae and public key using 2048 bits
     *
     * @return key pair
     * @throws NoSuchAlgorithmException
     */
    public static KeyPair generateKey() throws NoSuchAlgorithmException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ALGORITHM);
        keyGen.initialize(KEY_SIZE);
        KeyPair key = keyGen.generateKeyPair();
        return key;
    }

    public static int getKeySize() {
        return KEY_SIZE;
    }

    /**
     * Encrypt a text using public key.
     *
     * @param text the original unencrypted text
     * @param key the public key
     * @return encrypted text
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    public static byte[] encrypt(byte[] text, PublicKey key) throws
            NoSuchAlgorithmException,
            NoSuchPaddingException,
            InvalidKeyException,
            IllegalBlockSizeException,
            BadPaddingException {
        byte[] cipherText = null;
        // get an RSA cipher object and print the provider
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");

        // encrypt the plaintext using the public key
        cipher.init(Cipher.ENCRYPT_MODE, key);
        cipherText = cipher.doFinal(text);
        return cipherText;
    }

    /**
     * Encrypt a text using public key. The result is enctypted BASE64 encoded text
     *
     * @param text the original unencrypted text
     * @param key the public key
     * @return encrypted text encoded as BASE64
     * @throws UnsupportedEncodingException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    public static String encrypt(String text, PublicKey key) throws UnsupportedEncodingException,
            NoSuchAlgorithmException,
            NoSuchPaddingException,
            InvalidKeyException,
            IllegalBlockSizeException,
            BadPaddingException {
        return Base64.encodeBase64String(encrypt(text.getBytes("UTF8"),key));
    }

    /**
     * Decrypt text using private key
     *
     * @param text the encrypted text
     * @param key the private key
     * @return the unencrypted text
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    public static byte[] decrypt(byte[] text, PrivateKey key) throws
            NoSuchAlgorithmException,
            NoSuchPaddingException,
            InvalidKeyException,
            IllegalBlockSizeException,
            BadPaddingException {
        // decrypt the text using the private key
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(text);

    }

    /**
     * Decrypt BASE64 encoded text using private key
     *
     * @param text the encrypted text, encoded as BASE64
     * @param key the private key
     * @return the unencrypted text encoded as UTF8
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    public static String decrypt(String text, PrivateKey key) throws
            IOException,
            NoSuchAlgorithmException,
            NoSuchPaddingException,
            InvalidKeyException,
            IllegalBlockSizeException,
            BadPaddingException {
        // decrypt the text using the private key
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

    /**
     * Generates Private Key from BASE64 encoded string
     *
     * @param key BASE64 encoded string which represents the key
     * @return the PrivateKey
     * @throws NoSuchAlgorithmException
     * @throws IOException
     * @throws InvalidKeySpecException
     */
    public static PrivateKey getPrivateKeyFromString(String key) throws
            NoSuchAlgorithmException,
            InvalidKeySpecException {
        return KeyFactory.getInstance(ALGORITHM).generatePrivate(new PKCS8EncodedKeySpec(Base64.decodeBase64(key)));
    }

    /**
     * Generates Public Key from BASE64 encoded string
     *
     * @param key BASE64 encoded string which represents the key
     * @return the PublicKey
     * @throws NoSuchAlgorithmException
     * @throws IOException
     * @throws InvalidKeySpecException
     */
    public static PublicKey getPublicKeyFromString(String key) throws
            NoSuchAlgorithmException,
            InvalidKeySpecException {
        return KeyFactory.getInstance(ALGORITHM).generatePublic(new X509EncodedKeySpec(Base64.decodeBase64(key)));
    }
}