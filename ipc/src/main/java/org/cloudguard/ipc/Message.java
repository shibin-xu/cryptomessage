package org.cloudguard.ipc;

public class Message {

    private String body;                // Message content
    private String hashOfLastMessage;   // Hash of last message
    private String Signature;           // Mac of encryption result

    // TODO
    // TODO Encrypt then Mac
    // C         <-- Enc(AES.key, body || hashOfLastMessage)
    // Signature <-- Sign(RSA.priv, C)
    //
    // TODO On disk format
}
