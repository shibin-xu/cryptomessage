package org.cloudguard.commons;

import java.util.Objects;

public class GetRequest {
    private String cookie;

    public GetRequest(String cookie) {
        this.cookie = cookie;
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
        if (!(o instanceof GetRequest)) return false;
        GetRequest that = (GetRequest) o;
        return getCookie().equals(that.getCookie());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCookie());
    }

    @Override
    public String toString() {
        return "GetRequest{" +
                "cookie='" + cookie + '\'' +
                '}';
    }
}
