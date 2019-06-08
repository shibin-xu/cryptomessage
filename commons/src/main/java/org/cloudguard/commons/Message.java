package org.cloudguard.commons;

import java.util.Objects;

public class Message {

    private String body;                // AES encrypted Message content
    private String encryptedAESKey;     // RSA encrypted AES key
    private String hashOfLastMessage;   // Hash of last message
    private long time;                  // Timestamp of sending

    public Message(String body, String encryptedAESKey, String hashOfLastMessage, long time) {
        this.body = body;
        this.encryptedAESKey = encryptedAESKey;
        this.hashOfLastMessage = hashOfLastMessage;
        this.time = time;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getEncryptedAESKey() {
        return encryptedAESKey;
    }

    public void setEncryptedAESKey(String encryptedAESKey) {
        this.encryptedAESKey = encryptedAESKey;
    }

    public String getHashOfLastMessage() {
        return hashOfLastMessage;
    }

    public void setHashOfLastMessage(String hashOfLastMessage) {
        this.hashOfLastMessage = hashOfLastMessage;
    }

    public long getTime() {
        return time;
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
                getEncryptedAESKey().equals(message.getEncryptedAESKey()) &&
                getHashOfLastMessage().equals(message.getHashOfLastMessage());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getBody(), getEncryptedAESKey(), getHashOfLastMessage(), getTime());
    }

    @Override
    public String toString() {
        return "Message{" +
                "body='" + body + '\'' +
                ", encryptedAESKey='" + encryptedAESKey + '\'' +
                ", hashOfLastMessage='" + hashOfLastMessage + '\'' +
                ", time=" + time +
                '}';
    }
}
