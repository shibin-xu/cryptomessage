package org.cloudguard.crypto;

import org.apache.commons.codec.binary.Base64;

public class NameKey {
    /**
     * name = hash(hash(file) xor hash(username))
     * key = pub(AES)
     */
    public static final int SIZE = PasswordUtil.RESULT_SIZE + RSAEncryptUtil.RESULT_SIZE;
    public static final int NAME_SIZE = PasswordUtil.RESULT_SIZE;
    public static final int KEY_SIZE = RSAEncryptUtil.RESULT_SIZE;

    private byte[] name;
    private byte[] key;

    private NameKey() {
        // Disable default constructor
    }

    public NameKey(String name, String key) throws
            NullPointerException,
            IllegalArgumentException {
        setName(name);
        setKey(key);
    }

    public NameKey(byte[] name, byte[] key) throws
            NullPointerException,
            IllegalArgumentException {
        setName(name);
        setKey(key);
    }

    public void setName(byte[] name) throws
            NullPointerException,
            IllegalArgumentException {
        if (name == null)
            throw new NullPointerException("NameKey.setName() name must not be null");
        if (name.length != PasswordUtil.RESULT_SIZE)
            throw new IllegalArgumentException("NameKey.setName() name must have size = PasswordUtil.RESULT_SIZE not " + name.length);

        this.name = name;
    }

    public void setName(String name) throws
            NullPointerException,
            IllegalArgumentException {
        if (name == null)
            throw new NullPointerException("NameKey.setName() name must not be null");

        setName(Base64.decodeBase64(name));
    }

    public byte[] getName() {
        byte[] arr = new byte[name.length];
        System.arraycopy(name, 0, arr, 0, name.length);

        return arr;
    }

    public String getNameBase64() {
        return Base64.encodeBase64String(name);
    }

    public void setKey(byte[] key) throws
            NullPointerException,
            IllegalArgumentException {
        if (key == null)
            throw new NullPointerException("NameKey.setKey() key must not be null");
        if (key.length != RSAEncryptUtil.RESULT_SIZE)
            throw new IllegalArgumentException("NameKey.setName() key must have size = AESEncryptUtil.HASH_SIZE");

        this.key = key;
    }

    public void setKey(String key) throws
            NullPointerException,
            IllegalArgumentException {
        if (key == null)
            throw new NullPointerException("NameKey.setKey() key must not be null");

        setKey(Base64.decodeBase64(key));
    }

    public byte[] getKey() {
        byte[] arr = new byte[key.length];
        System.arraycopy(key, 0, arr, 0, key.length);

        return arr;
    }

    public String getKeyBase64() {
        return Base64.encodeBase64String(key);
    }

    public static byte[] xor(byte[] arr1, byte[] arr2) throws
            NullPointerException,
            IllegalArgumentException {
        if (arr1 == null)
            throw new NullPointerException("NameKey.xor() arr1 must not be null");
        if (arr2 == null)
            throw new NullPointerException("NameKey.xor() arr2 must not be null");
        if (arr1.length != arr2.length)
            throw new IllegalArgumentException("NameKey.xor() arr1 and arr2 must have same length");

        byte[] ret = new byte[arr1.length];
        for (int i = 0; i < ret.length; i++)
            ret[i] = (byte)((int)arr1[i] ^ (int)arr2[i]);

        return ret;
    }
}
