package org.cloudguard.commons;

import java.util.Objects;

public class LoginPrepareRequest {
    private String publicKey;
    private String nonce;

    public LoginPrepareRequest(String publicKey, String nonce) {
        this.publicKey = publicKey;
        this.nonce = nonce;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LoginPrepareRequest)) return false;
        LoginPrepareRequest that = (LoginPrepareRequest) o;
        return getPublicKey().equals(that.getPublicKey()) &&
                getNonce().equals(that.getNonce());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPublicKey(), getNonce());
    }

    @Override
    public String toString() {
        return "LoginPrepareRequest{" +
                "publicKey='" + publicKey + '\'' +
                ", nonce='" + nonce + '\'' +
                '}';
    }
}
