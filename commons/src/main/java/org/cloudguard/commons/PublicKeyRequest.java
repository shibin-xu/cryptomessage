package org.cloudguard.commons;

import java.util.List;
import java.util.Objects;

public class PublicKeyRequest {
    private String token;
    private List<String> usernames;

    public PublicKeyRequest(String token, List<String> usernames) {
        this.token = token;
        this.usernames = usernames;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public List<String> getUsernames() {
        return usernames;
    }

    public void setUsernames(List<String> usernames) {
        this.usernames = usernames;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PublicKeyRequest)) return false;
        PublicKeyRequest that = (PublicKeyRequest) o;
        return getToken().equals(that.getToken()) &&
                getUsernames().equals(that.getUsernames());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getToken(), getUsernames());
    }

    @Override
    public String toString() {
        return "PublicKeyRequest{" +
                "token='" + token + '\'' +
                ", usernames=" + usernames +
                '}';
    }
}
