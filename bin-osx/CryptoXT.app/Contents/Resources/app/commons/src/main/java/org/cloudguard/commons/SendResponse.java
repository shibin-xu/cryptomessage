package org.cloudguard.commons;

import java.util.Objects;

public class SendResponse {
    boolean success;

    public SendResponse(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SendResponse)) return false;
        SendResponse that = (SendResponse) o;
        return isSuccess() == that.isSuccess();
    }

    @Override
    public int hashCode() {
        return Objects.hash(isSuccess());
    }

    @Override
    public String toString() {
        return "SendResponse{" +
                "success=" + success +
                '}';
    }
}
