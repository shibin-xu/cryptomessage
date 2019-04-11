package org.cloudguard.commons;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class PublicKeyResponse {
    private boolean success;
    private Map<String, String> usernameToPublickey;

    public PublicKeyResponse(boolean success, Map<String, String> usernameToPublickey) {
        this.success = success;
        this.usernameToPublickey = usernameToPublickey;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Map<String, String> getUsernameToPublickey() {
        return usernameToPublickey;
    }

    public void setUsernameToPublickey(Map<String, String> usernameToPublickey) {
        this.usernameToPublickey = usernameToPublickey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PublicKeyResponse)) return false;
        PublicKeyResponse that = (PublicKeyResponse) o;
        return isSuccess() == that.isSuccess() &&
                getUsernameToPublickey().equals(that.getUsernameToPublickey());
    }

    @Override
    public int hashCode() {
        return Objects.hash(isSuccess(), getUsernameToPublickey());
    }

    @Override
    public String toString() {
        return "PublicKeyResponse{" +
                "success=" + success +
                ", usernameToPublickey=" + usernameToPublickey +
                '}';
    }
}
