package org.cloudguard.ipc;

import java.util.Objects;

public class Relay {

    private RelayType relayType;      
    private String relayContent;   
    private String senderID;
    private long time;             

    public Relay(RelayType relayType, String relayContent, String senderID, long time) {
        this.relayType = relayType;
        this.relayContent = relayContent;
        this.senderID = senderID;
        this.time = time;
    }

    public RelayType getType() {
        return this.relayType;
    }

    public String getContent() {
        return this.relayContent;
    }

    public String getSenderID() {
        return this.senderID;
    }

    public long getTime() {
        return this.time;
    }

    @Override
    public String toString() {
        return "Relay{" +
                "type='" + relayType + '\'' +
                ", content='" + relayContent + '\'' +
                ", sender='" + senderID + '\'' +
                ", time=" + time +
                '}';
    }

}
