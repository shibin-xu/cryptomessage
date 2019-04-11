package org.cloudvault.commons;

import java.util.Objects;

public class Request {
    private String className;
    private String json;

    public Request(String className, String json) {
        this.className = className;
        this.json = json;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Request)) return false;
        Request request = (Request) o;
        return getClassName().equals(request.getClassName()) &&
                getJson().equals(request.getJson());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClassName(), getJson());
    }

    @Override
    public String toString() {
        return "Request{" +
                "className='" + className + '\'' +
                ", json='" + json + '\'' +
                '}';
    }
}
