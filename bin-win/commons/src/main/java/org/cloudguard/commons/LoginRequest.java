package org.cloudguard.commons;

import java.util.Objects;

public class LoginRequest {
    private String email;
    private String passowrd;

    public LoginRequest(String email, String passowrd) {
        this.email = email;
        this.passowrd = passowrd;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassowrd() {
        return passowrd;
    }

    public void setPassowrd(String passowrd) {
        this.passowrd = passowrd;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LoginRequest)) return false;
        LoginRequest that = (LoginRequest) o;
        return getEmail().equals(that.getEmail()) &&
                getPassowrd().equals(that.getPassowrd());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getEmail(), getPassowrd());
    }

    @Override
    public String toString() {
        return "LoginRequest{" +
                "email='" + email + '\'' +
                ", passowrd='" + passowrd + '\'' +
                '}';
    }
}
