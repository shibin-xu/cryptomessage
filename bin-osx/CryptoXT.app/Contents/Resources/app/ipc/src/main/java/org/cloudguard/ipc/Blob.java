package org.cloudguard.ipc;
import org.cloudguard.commons.*;
import java.util.Map;
import java.util.List;
import java.util.Objects;

public class Blob {

    private Map<String, String> contacts;
    private Map<String, String> lastHashes;
    private Map<String, List<Envelope>> envelopeMap;
    private Map<String, List<Speech>> speechMap;

    public Blob(Map<String, String> contacts, Map<String, String> lastHashes, 
        Map<String, List<Envelope>> envelopeMap, Map<String, List<Speech>> speechMap) {
        this.contacts = contacts;
        this.lastHashes = lastHashes;
        this.envelopeMap = envelopeMap;
        this.speechMap = speechMap;
    }

    public Map<String, String> getContacts() { return contacts; }
    public Map<String, String> getLastHashes() { return lastHashes; }
    public Map<String, List<Envelope>> getEnvelopeMap() { return envelopeMap; }
    public Map<String, List<Speech>> getSpeechMap() { return speechMap; }


}
