package org.cloudguard.commons;

import java.util.Objects;

public class LoginFinishResponse {
    private boolean success;
    private String token;

    public LoginFinishResponse(boolean success, String token) {
        this.success = success;
        this.token = token;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LoginFinishResponse)) return false;
        LoginFinishResponse that = (LoginFinishResponse) o;
        return isSuccess() == that.isSuccess() &&
                getToken().equals(that.getToken());
    }

    @Override
    public int hashCode() {
        return Objects.hash(isSuccess(), getToken());
    }

    @Override
    public String toString() {
        return "LoginFinishResponse{" +
                "success=" + success +
                ", token='" + token + '\'' +
                '}';
    }
}
