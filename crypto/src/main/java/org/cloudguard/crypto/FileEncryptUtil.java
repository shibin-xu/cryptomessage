package org.cloudguard.crypto;

import com.google.gson.Gson;
import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.cloudguard.commons.ClientUtil;
import org.cloudguard.commons.DecryptAESResponse;
import org.cloudguard.commons.PublicKeyResponse;
import org.cloudguard.commons.Response;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.rmi.ServerException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.List;

import static org.cloudguard.commons.ClientUtil.queryuser;

public class FileEncryptUtil {
    private FileEncryptUtil() {
        // Disable default constructor
    }

    /**
     * Encrypt input bytes from in, and save result to output out.
     *
     * @param in input stream
     * @param out output stream
     * @param AESkey the AES-256 key
     * @param usernames the recipient(s) of encrypted file
     * @return
     * @throws IOException
     * @throws InvalidCipherTextException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws NoSuchProviderException
     * @throws InvalidKeySpecException
     */
    public static byte[] encrypt(RandomAccessFile in, RandomAccessFile out, byte[] AESkey,
                                 String token, List<String> usernames) throws
            IOException,
            InvalidCipherTextException,
            NoSuchAlgorithmException,
            NoSuchPaddingException,
            InvalidKeyException,
            IllegalBlockSizeException,
            BadPaddingException,
            NoSuchProviderException,
            InvalidKeySpecException,
            ClassNotFoundException {

        Header header = new Header();
        int offset = 0;
        byte[] buffer = new byte[CryptoUtil.BUFFER_SIZE];
        int read;

        while ((read = in.read(buffer)) > 0) {
            byte[] arr = new byte[read];
            System.arraycopy(buffer, 0, arr, 0, read);
            byte[] encrypted = AESEncryptUtil.encrypt(arr, AESkey);

            header.add(offset, encrypted.length);
            offset += encrypted.length;
            out.write(encrypted);
        }

        byte[] filehash = PasswordUtil.hash(out, 0, offset);
        out.seek(offset);

        if (usernames != null) {
            Response response = queryuser(token, usernames);
            Gson gson = new Gson();
            PublicKeyResponse publicKeyResponse = (PublicKeyResponse) gson.fromJson(response.getJson(),
                    Class.forName(response.getClassName()));
            if (!publicKeyResponse.isSuccess())
                throw new ServerException("Retrieving public key for " + usernames + " from server failed");

            for (String each : usernames) {
                byte[] name = PasswordUtil.hash(NameKey.xor(filehash, PasswordUtil.hash(each.getBytes())));
                byte[] key = RSAEncryptUtil.encrypt(Base64.encodeBase64String(AESkey).getBytes(),
                        RSAEncryptUtil.getPublicKeyFromString(publicKeyResponse.getUsernameToPublickey().get(each)));
                header.addNameKey(new NameKey(name, key));
            }
        }

        header.setPointer(offset);
        out.write(header.toBytes());

        return filehash;
    }

    /**
     * Get NameKey from input in.
     *
     * Assumes file format:
     *                      |-----------|
     *                      | AES(file) |
     *                      |           |
     *                      |-----------|
     *                      | header    | --> {name, pub(AES)}
     *                      |-----------|
     *
     * @param in input stream
     * @param username one of the recipient(s)
     * @return NameKey corresponds to username
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     */
    public static NameKey getNameKey(RandomAccessFile in, String username) throws
            IOException,
            NoSuchAlgorithmException,
            NoSuchProviderException{
        in.seek(0);
        Header header = new Header(in);
        List<NameKey> nameKeys = header.getNameKeys();
        int offset = header.getPointer();
        byte[] filehash = PasswordUtil.hash(in, 0, offset);

        byte[] name = PasswordUtil.hash(NameKey.xor(filehash, PasswordUtil.hash(username.getBytes())));
        for (NameKey each : nameKeys) {
            if (Arrays.equals(name, each.getName()))
                return each;
        }

        throw new IllegalArgumentException("FileEncryptUtil.getNameKey() username is not recipient");
    }

    /**
     * Decrypt input bytes from in, and save result to output out.
     *
     * Assumes file format:
     *                      |-----------|
     *                      | AES(file) |
     *                      |           |
     *                      |-----------|
     *                      | header    | --> {name, pub(AES)}
     *                      |-----------|
     *
     * @param in input stream
     * @param out output stream
     * @param username recipient credential
     * @param token recipient credential
     * @throws IOException
     * @throws InvalidCipherTextException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     */
    public static void decrypt(RandomAccessFile in, RandomAccessFile out, String username, String token) throws
            IOException,
            InvalidCipherTextException,
            NoSuchAlgorithmException,
            NoSuchProviderException,
            ClassNotFoundException {

        Header header = new Header(in);
        NameKey nameKey = getNameKey(in, username);

        Response response = ClientUtil.decryptSymmetric(token, Base64.encodeBase64String(nameKey.getKey()));
        Gson gson = new Gson();
        DecryptAESResponse decryptAESResponse =
                (DecryptAESResponse) gson.fromJson(response.getJson(), Class.forName(response.getClassName()));

        if (!decryptAESResponse.isSuccess())
            throw new ServerException("Decrypting AES key for " + username + " from server failed");
        byte[] key = Base64.decodeBase64(decryptAESResponse.getDecryptedAESKey());

        List<Integer> positions = header.getPositions();
        List<Integer> lengths = header.getLengths();

        for (int i = 0; i < positions.size(); i++) {
            int position = positions.get(i);
            int length = lengths.get(i);

            in.seek(position);
            byte[] arr = new byte[length];
            in.read(arr);

            byte[] decrypted = AESEncryptUtil.decrypt(arr, key);
            out.write(decrypted);
        }
    }

    /**
     * Computes SHA3-512 hash of AES(file) part of input file.
     *
     * Assumes file format:
     *                      |-----------|
     *                      | AES(file) |
     *                      |           |
     *                      |-----------|
     *                      | header    |
     *                      |-----------|
     *
     * @param in input file
     * @return filehash
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     */
    public static byte[] getFileHash(RandomAccessFile in) throws
            IOException,
            NoSuchAlgorithmException,
            NoSuchProviderException {
        Header header = new Header(in);
        int offset = header.getPointer();

        in.seek(0L);
        return PasswordUtil.hash(in, 0, offset);
    }
}
