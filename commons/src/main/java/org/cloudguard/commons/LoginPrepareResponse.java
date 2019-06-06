package org.cloudguard.commons;

import java.util.Objects;

public class LoginPrepareResponse {
    private String nonce;

    public LoginPrepareResponse(String nonce) {
        this.nonce = nonce;
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
        if (!(o instanceof LoginPrepareResponse)) return false;
        LoginPrepareResponse that = (LoginPrepareResponse) o;
        return getNonce().equals(that.getNonce());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getNonce());
    }

    @Override
    public String toString() {
        return "LoginPrepareResponse{" +
                "nonce='" + nonce + '\'' +
                '}';
    }
}
