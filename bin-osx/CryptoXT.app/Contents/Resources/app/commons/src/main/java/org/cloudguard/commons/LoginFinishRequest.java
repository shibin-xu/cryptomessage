package org.cloudguard.commons;

import java.util.Objects;

public class LoginFinishRequest {
    private String publicKey;
    private String signature;

    public LoginFinishRequest(String publicKey, String signature) {
        this.publicKey = publicKey;
        this.signature = signature;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LoginFinishRequest)) return false;
        LoginFinishRequest that = (LoginFinishRequest) o;
        return getPublicKey().equals(that.getPublicKey()) &&
                getSignature().equals(that.getSignature());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPublicKey(), getSignature());
    }

    @Override
    public String toString() {
        return "LoginFinishRequest{" +
                "publicKey='" + publicKey + '\'' +
                ", signature='" + signature + '\'' +
                '}';
    }
}
