package org.cloudvault.commons;

import java.util.Objects;

public class DecryptAESResponse {
    private boolean success;
    private String decryptedAESKey;

    public DecryptAESResponse(boolean success, String decryptedAESKey) {
        this.success = success;
        this.decryptedAESKey = decryptedAESKey;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getDecryptedAESKey() {
        return decryptedAESKey;
    }

    public void setDecryptedAESKey(String decryptedAESKey) {
        this.decryptedAESKey = decryptedAESKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DecryptAESResponse)) return false;
        DecryptAESResponse that = (DecryptAESResponse) o;
        return isSuccess() == that.isSuccess() &&
                getDecryptedAESKey().equals(that.getDecryptedAESKey());
    }

    @Override
    public int hashCode() {
        return Objects.hash(isSuccess(), getDecryptedAESKey());
    }

    @Override
    public String toString() {
        return "DecryptAESResponse{" +
                "success=" + success +
                ", decryptedAESKey='" + decryptedAESKey + '\'' +
                '}';
    }
}
