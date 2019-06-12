package org.cloudguard.commons;

import java.util.List;
import java.util.Objects;

public class SendRequest {
    private List<Envelope> envelopes;

    public SendRequest(List<Envelope> envelopes) {
        this.envelopes = envelopes;
    }

    public List<Envelope> getEnvelopes() {
        return envelopes;
    }

    public void setEnvelopes(List<Envelope> envelopes) {
        this.envelopes = envelopes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SendRequest)) return false;
        SendRequest that = (SendRequest) o;
        return getEnvelopes().equals(that.getEnvelopes());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getEnvelopes());
    }

    @Override
    public String toString() {
        return "SendRequest{" +
                "envelopes=" + envelopes +
                '}';
    }
}
