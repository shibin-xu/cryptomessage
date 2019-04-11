package org.cloudguard.crypto;

import org.apache.commons.codec.binary.Base64;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;

public class PasswordUtil {
    public static final int RESULT_SIZE = 64;

    protected static final String ALGORITHM = "SHA3-512";
    protected static final String PROVIDER= "BC";
    // key length = KEY_SIZE * 8 bits
    protected static final int KEY_SIZE = 16;
    protected static final int SEED_SIZE = 1024;

    private static final SecureRandom PRNG = new SecureRandom();

    private PasswordUtil() {
        // Disable default constructor
    }

    /**
     * Hash text WITHOUT salt.
     *
     * @param text
     * @return hash
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     */
    public static byte[] hash(byte[] text) throws
            NoSuchAlgorithmException,
            NoSuchProviderException {
        MessageDigest SHA3 = MessageDigest.getInstance(ALGORITHM, PROVIDER);
        return SHA3.digest(text);
    }

    /**
     * Hash input from start to end WITHOUT salt.
     *
     * @param in input stream
     * @param start inclusive
     * @param end exclusive
     * @return hash
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     * @throws IOException
     */
    public static byte[] hash(RandomAccessFile in, int start, int end) throws
            NoSuchAlgorithmException,
            NoSuchProviderException,
            IOException {
        in.seek(start);

        MessageDigest SHA3 = MessageDigest.getInstance(ALGORITHM, PROVIDER);
        byte[] buffer = new byte[CryptoUtil.BUFFER_SIZE];
        int read;
        while ((start < end) && (read = in.read(buffer, 0, Math.min(CryptoUtil.BUFFER_SIZE, end-start))) > 0) {
            SHA3.update(buffer, 0, read);
            start += read;
        }

        return SHA3.digest();
    }

    /**
     * Hash text WITHOUT salt.
     *
     * @param text
     * @return hash
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     */
    public static String hash(String text) throws
            NoSuchAlgorithmException,
            NoSuchProviderException {
        return hashToString(hash(text.getBytes()));
    }

    /**
     * Compute the hex representation of hash
     *
     * @param hash
     * @return hash as hex string
     */
    public static String hashToString(byte[] hash) {
        StringBuffer buff = new StringBuffer();

        for (byte b : hash)
            buff.append(String.format("%02x", b & 0xFF));

        return buff.toString();
    }

    /**
     * Hash text with salt. If hs = null, then generate random salt.
     *
     * @param text
     * @param hs
     * @return hash with salt
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     */
    public static HashSalt hash(byte[] text, HashSalt hs) throws
            NoSuchAlgorithmException,
            NoSuchProviderException {
        byte[] salt;

        if (hs == null) {
            salt = new byte[KEY_SIZE];
            PRNG.nextBytes(salt);
        } else {
            salt = Base64.decodeBase64(hs.getSalt());
        }

        MessageDigest SHA3 = MessageDigest.getInstance(ALGORITHM, PROVIDER);
        SHA3.update(salt);
        return new HashSalt(hashToString(SHA3.digest(text)), Base64.encodeBase64String(salt));
    }

    /**
     * Hash text with salt. If hs = null, then generate random salt.
     *
     * @param text
     * @param hs
     * @return hash with salt
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     */
    public static HashSalt hash(String text, HashSalt hs) throws
            NoSuchAlgorithmException,
            NoSuchProviderException {
        return hash(text.getBytes(), hs);
    }

    public static String generateCookie() throws
            NoSuchAlgorithmException,
            NoSuchProviderException {
        byte[] seed = new byte[SEED_SIZE];
        PRNG.nextBytes(seed);

        return hashToString(hash(seed));
    }
}
