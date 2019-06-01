package org.cloudguard.ipc;

import java.util.Objects;

public class Relay {

    private RelayType relayType;      
    private String primary;      
    private String secondary;   
    private long time;             

    public Relay(RelayType relayType, String primary, String secondary, long time) {
        this.relayType = relayType;
        this.primary = primary;
        this.secondary = secondary;
        this.time = time;
    }

    public RelayType getType() {
        return this.relayType;
    }

    public String getPrimaryData() {
        return this.primary;
    }

    public String getSecondaryData() {
        return this.secondary;
    }

    public long getTime() {
        return this.time;
    }

    @Override
    public String toString() {
        return "Relay{" +
                "type='" + relayType + '\'' +
                ", primary='" + primary + '\'' +
                ", secondary='" + secondary + '\'' +
                ", time=" + time +
                '}';
    }

}
