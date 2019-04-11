package org.cloudvault.ui;

import java.security.PublicKey;
import java.util.Objects;

/**
 * User information after login
 */
public class CG_user {
    private PublicKey publicKey;
    private String username;
    private String cookie;

    public CG_user(PublicKey publicKey, String username, String cookie) {
        this.publicKey = publicKey;
        this.username = username;
        this.cookie = cookie;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCookie() {
        return cookie;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CG_user)) return false;
        CG_user cg_user = (CG_user) o;
        return getPublicKey().equals(cg_user.getPublicKey()) &&
                getUsername().equals(cg_user.getUsername()) &&
                getCookie().equals(cg_user.getCookie());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPublicKey(), getUsername(), getCookie());
    }

    @Override
    public String toString() {
        return "CG_user{" +
                "publicKey=" + publicKey +
                ", username='" + username + '\'' +
                ", cookie='" + cookie + '\'' +
                '}';
    }
}
