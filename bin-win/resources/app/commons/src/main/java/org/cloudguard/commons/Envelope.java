package org.cloudguard.commons;

import java.util.Objects;

public class Envelope {
    private Message message;                // Message inside
    private String signature;               // Mac of encryption result
    private String recipientRSAPublicKey;   // RSA public key of recipient

    public Envelope(Message message, String signature, String recipientRSAPublicKey) {
        this.message = message;
        this.signature = signature;
        this.recipientRSAPublicKey = recipientRSAPublicKey;
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

    public String getRecipientRSAPublicKey() {
        return recipientRSAPublicKey;
    }

    public void setRecipientRSAPublicKey(String recipientRSAPublicKey) {
        this.recipientRSAPublicKey = recipientRSAPublicKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Envelope)) return false;
        Envelope envelope = (Envelope) o;
        return getMessage().equals(envelope.getMessage()) &&
                getSignature().equals(envelope.getSignature()) &&
                getRecipientRSAPublicKey().equals(envelope.getRecipientRSAPublicKey());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getMessage(), getSignature(), getRecipientRSAPublicKey());
    }

    @Override
    public String toString() {
        return "Envelope{" +
                "message=" + message +
                ", signature='" + signature + '\'' +
                ", recipientRSAPublicKey='" + recipientRSAPublicKey + '\'' +
                '}';
    }
}
