package org.cloudguard.ipc;

import java.util.Objects;

public class Envelope {
    private Message message;    // Message inside
    private String signature;   // Mac of encryption result

    public Envelope(Message message, String signature) {
        this.message = message;
        this.signature = signature;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Envelope)) return false;
        Envelope envelope = (Envelope) o;
        return getMessage().equals(envelope.getMessage()) &&
                getSignature().equals(envelope.getSignature());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getMessage(), getSignature());
    }

    @Override
    public String toString() {
        return "Envelope{" +
                "message=" + message +
                ", signature='" + signature + '\'' +
                '}';
    }
}
