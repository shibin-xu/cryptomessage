package org.cloudguard.commons;

import java.util.Objects;

public class DecryptAESRequest {
    private String token;
    private String encryptedAESKey;

    public DecryptAESRequest(String token, String encryptedAESKey) {
        this.token = token;
        this.encryptedAESKey = encryptedAESKey;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getEncryptedAESKey() {
        return encryptedAESKey;
    }

    public void setEncryptedAESKey(String encryptedAESKey) {
        this.encryptedAESKey = encryptedAESKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DecryptAESRequest)) return false;
        DecryptAESRequest that = (DecryptAESRequest) o;
        return getToken().equals(that.getToken()) &&
                getEncryptedAESKey().equals(that.getEncryptedAESKey());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getToken(), getEncryptedAESKey());
    }

    @Override
    public String toString() {
        return "DecryptAESRequest{" +
                "token='" + token + '\'' +
                ", encryptedAESKey='" + encryptedAESKey + '\'' +
                '}';
    }
}
