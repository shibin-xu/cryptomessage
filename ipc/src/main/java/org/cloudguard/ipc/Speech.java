package org.cloudguard.ipc;

import java.util.Objects;

public class Speech {

    private String identifier;
    private String senderKey; 
    private String recipientKey;       
    private String content;   
    private boolean previousVerified;
    private boolean signatureVerified;
    private long time;             

    public Speech(String identifier, String senderKey, String recipientKey, String content, boolean previous, boolean signature, long time) {
        this.identifier = identifier;
        this.senderKey = senderKey;
        this.recipientKey = recipientKey;
        this.content = content;
        this.previousVerified = previous;
        this.signatureVerified = signature;
        this.time = time;
    }


    public String getIdentifier() {
        return this.identifier;
    }
    public String getSenderKey() {
        return this.senderKey;
    }
    public String getRecipientKey() {
        return this.recipientKey;
    }
    public String getContent() {
        return this.content;
    }

    public boolean IsPreviousVerified() {
        return this.previousVerified;
    }

    public boolean IsSignatureVerified() {
        return this.signatureVerified;
    }

    public long getTime() {
        return this.time;
    }

    @Override
    public String toString() {
        return "Speech{" +
                "identifier='" + identifier + '\'' +
                ", content='" + content + '\'' +
                ", time=" + time + '\'' +
                '}';
    }

}
