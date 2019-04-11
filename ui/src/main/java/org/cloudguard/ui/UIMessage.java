package org.cloudvault.ui;

import java.util.Objects;

public class UIMessage {
    private boolean success;
    private String message;

    public UIMessage(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
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
        if (!(o instanceof UIMessage)) return false;
        UIMessage uiMessage = (UIMessage) o;
        return isSuccess() == uiMessage.isSuccess() &&
                getMessage().equals(uiMessage.getMessage());
    }

    @Override
    public int hashCode() {
        return Objects.hash(isSuccess(), getMessage());
    }

    @Override
    public String toString() {
        return "UIMessage{" +
                "success=" + success +
                ", message='" + message + '\'' +
                '}';
    }
}
