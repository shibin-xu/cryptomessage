package org.cloudguard.ipc;

import java.util.Objects;

public class Message {

    private String body;                // Message content
    private String hashOfLastMessage;   // Hash of last message
    private long time;                  // Timestamp of sending

    // TODO

    public Message(String body, String hashOfLastMessage, long time) {
        this.body = body;
        this.hashOfLastMessage = hashOfLastMessage;
        this.time = time;
    }

    public String getBody() {
        return body;
    }

    public String getHashOfLastMessage() {
        return hashOfLastMessage;
    }

    public long getTime() {
        return time;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setHashOfLastMessage(String hashOfLastMessage) {
        this.hashOfLastMessage = hashOfLastMessage;
    }

    public void setTime(long time) {
        this.time = time;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Message)) return false;
        Message message = (Message) o;
        return getTime() == message.getTime() &&
                getBody().equals(message.getBody()) &&
                getHashOfLastMessage().equals(message.getHashOfLastMessage());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getBody(), getHashOfLastMessage(), getTime());
    }

    @Override
    public String toString() {
        return "Message{" +
                "body='" + body + '\'' +
                ", hashOfLastMessage='" + hashOfLastMessage + '\'' +
                ", time=" + time +
                '}';
    }

    // TODO Encrypt then Mac
    // C         <-- Enc(AES.key, body || hashOfLastMessage)
    // Signature <-- Sign(RSA.priv, C)
    //
    // TODO On disk format
}
