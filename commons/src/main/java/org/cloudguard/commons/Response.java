package org.cloudvault.commons;

import java.util.Objects;

public class Response {
    private String className;
    private String json;

    public Response(String className, String json) {
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
        if (!(o instanceof Response)) return false;
        Response response = (Response) o;
        return getClassName().equals(response.getClassName()) &&
                getJson().equals(response.getJson());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClassName(), getJson());
    }

    @Override
    public String toString() {
        return "Response{" +
                "className='" + className + '\'' +
                ", json='" + json + '\'' +
                '}';
    }
}
