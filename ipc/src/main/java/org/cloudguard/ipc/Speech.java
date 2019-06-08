package org.cloudguard.ipc;

import java.util.Objects;

public class Speech {

    private int identifier;
    private String senderKey; 
    private String recipientKey;     
    private String contactAlias;      
    private String content;   
    private boolean previousVerified;
    private boolean signatureVerified;
    private long time;             

    public Speech(int identifier, String senderKey,  String contactAlias, String content, long time) {
        this.identifier = identifier;
        this.senderKey = senderKey;
        this.recipientKey = "";
        this.contactAlias = contactAlias;
        this.content = content;
        this.previousVerified = false;
        this.signatureVerified = false;
        this.time = time;
    }

    public void Destination(String recipientKey) {
        this.recipientKey = recipientKey;
    }

    public void Verify(boolean previous, boolean signature) {
        if(previous)
        {
            this.previousVerified = true;
        }
        if(signature)
        {
            this.signatureVerified = true;
        }
    }

    public int getIdentifier() {
        return this.identifier;
    }
    public String getSenderKey() {
        return this.senderKey;
    }
    public String getRecipientKey() {
        return this.recipientKey;
    }
    public String getDisplay() {
        if (this.contactAlias.length() > 1) {
            return this.contactAlias;
        }
        return this.senderKey;
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
