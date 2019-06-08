package org.cloudguard.commons;

import java.util.List;
import java.util.Objects;

public class GetResponse {
    private List<Envelope> envelopes;

    public GetResponse(List<Envelope> envelopes) {
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
        if (!(o instanceof GetResponse)) return false;
        GetResponse that = (GetResponse) o;
        return getEnvelopes().equals(that.getEnvelopes());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getEnvelopes());
    }

    @Override
    public String toString() {
        return "GetResponse{" +
                "envelopes=" + envelopes +
                '}';
    }
}
