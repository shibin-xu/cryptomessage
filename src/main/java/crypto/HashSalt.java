package crypto;

public class HashSalt {
    private String hash; // hex string of hash result from SHA3-512
    private String salt; // base64 encoded string salt used to generate hash

    private HashSalt() {
        // Disable default constructor
    }

    /**
     * Construct a HashSalt.
     *
     * @param hash the hash to use
     * @param salt the salt to use
     */
    public HashSalt(String hash, String salt) throws IllegalArgumentException {
        setHash(hash);
        setSalt(salt);
    }

    /**
     * Set hash of this HashSalt.
     *
     * @param hash the hash to use
     * @throws IllegalArgumentException
     */
    public void setHash(String hash) throws IllegalArgumentException {
        if (hash == null)
            throw new IllegalArgumentException("HashSalt() hash must not be null");
        this.hash = hash;
    }

    /**
     * Set salt of this HashSalt.
     *
     * @param salt the salt to use
     * @throws IllegalArgumentException
     */
    public void setSalt(String salt) throws IllegalArgumentException {
        if (salt == null)
            throw new IllegalArgumentException("HashSalt() salt must not be null");
        this.salt = salt;
    }

    /**
     * @return hash
     */
    public String getHash() {
        return hash;
    }

    /**
     * @return salt
     */
    public String getSalt() {
        return salt;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof HashSalt)) {
            return false;
        }

        return this.hash.equals(((HashSalt) o).hash) && this.salt.equals(((HashSalt) o).salt);
    }
}
