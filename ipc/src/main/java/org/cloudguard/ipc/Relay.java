package org.cloudguard.ipc;

import java.util.Objects;

public class Relay {

    private RelayType relayType;      
    private String relayContent;   
    private long time;             

    public Relay(RelayType relayType, String relayContent, long time) {
        this.relayType = relayType;
        this.relayContent = relayContent;
        this.time = time;
    }

    public RelayType getType() {
        return relayType;
    }

    public String getContent() {
        return relayContent;
    }

    @Override
    public String toString() {
        return "Relay{" +
                "type='" + relayType + '\'' +
                ", content='" + relayContent + '\'' +
                ", time=" + time +
                '}';
    }

}
