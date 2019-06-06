package org.cloudguard.commons;

import java.util.Objects;

public class LoginFinishRequest {
    private String concatenateNonce;
    private String signature;

    public LoginFinishRequest(String concatenateNonce, String signature) {
        this.concatenateNonce = concatenateNonce;
        this.signature = signature;
    }

    public String getConcatenateNonce() {
        return concatenateNonce;
    }

    public void setConcatenateNonce(String concatenateNonce) {
        this.concatenateNonce = concatenateNonce;
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
        return getConcatenateNonce().equals(that.getConcatenateNonce()) &&
                getSignature().equals(that.getSignature());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getConcatenateNonce(), getSignature());
    }

    @Override
    public String toString() {
        return "LoginFinishRequest{" +
                "concatenateNonce='" + concatenateNonce + '\'' +
                ", signature='" + signature + '\'' +
                '}';
    }
}
