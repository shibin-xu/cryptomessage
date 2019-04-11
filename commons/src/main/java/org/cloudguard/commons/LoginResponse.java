package org.cloudguard.commons;

import java.util.Objects;

public class LoginResponse {
    private boolean success;
    private String token;
    private String message;

    public LoginResponse(boolean success, String token, String message) {
        this.success = success;
        this.token = token;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LoginResponse)) return false;
        LoginResponse that = (LoginResponse) o;
        return isSuccess() == that.isSuccess() &&
                getToken().equals(that.getToken()) &&
                getMessage().equals(that.getMessage());
    }

    @Override
    public int hashCode() {
        return Objects.hash(isSuccess(), getToken(), getMessage());
    }

    @Override
    public String toString() {
        return "LoginResponse{" +
                "success=" + success +
                ", token='" + token + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
