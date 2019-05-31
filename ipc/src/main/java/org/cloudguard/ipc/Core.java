package org.cloudguard.ipc;

import com.google.gson.Gson;
import org.cloudguard.crypto.CryptoUtil;
import org.cloudguard.crypto.PasswordUtil;
import org.cloudguard.crypto.RSAEncryptUtil;

import java.security.*;
import java.util.Date;

import org.zeromq.ZMQ;
import org.zeromq.ZContext;

public class Core {
    
    private Gson gson;
    private KeyPair keyPair;
    private PublicKey publicKey;
    private PrivateKey privateKey;
    private ZMQ.Socket zsocket;
    private Date date;
    private String hashOfLastMessage;

    private Relay ReadRelay(byte[] raw) {
        
        String rawString = new String(raw, ZMQ.CHARSET);
        Relay r = gson.fromJson(rawString, Relay.class);
        return r;
    }

    private void SendRelay(ZMQ.Socket zsocket, RelayType type, String payload, String sender, Date date) {
        
        Relay relay = new Relay(type, payload, sender, 2);
        String serialized = gson.toJson(relay);
        System.out.println("CRYPTOSend: " + serialized);
        zsocket.send(serialized.getBytes(ZMQ.CHARSET), 0);
    }

    private void ConnectToServer() {
        // internet socket stuff
        // ..

        // front end relay
        SendRelay(zsocket, RelayType.UIConfirmConnect, "4.4.4.255", "0", date);
                        
    }

    private void DisconnectFromServer() {
        // internet socket stuff
        // ..

        // front end relay
        SendRelay(zsocket, RelayType.UIConfirmDisconnect, "0.1.2.3", "0", date);
                        
    }

    private void SendMessage(Relay relay) {
        
        try {
         
            Message message = new Message(relay.getContent(), this.hashOfLastMessage, date.getTime());
            String text = gson.toJson(message);
            String proof = RSAEncryptUtil.sign(text, this.privateKey);
            Envelope envelope = new Envelope(message, proof);
            
            this.hashOfLastMessage = PasswordUtil.hash(text);

            // internet socket stuff
            // ..

            // front end relay
            SendRelay(zsocket, RelayType.UIConfirmSend, relay.getContent(), "0", this.date);   
        }
        catch(Exception e)
        {

        }

    }

    private void FakeRcMessage() {
        
        // TODO, change this to  automatic whenever a packet comes in
        try
        {
            String body = "This is a test.";
            Message expected = new Message(body, this.hashOfLastMessage, date.getTime());
            String serialized = gson.toJson(expected);
            Message actual = gson.fromJson(serialized, Message.class);

            // front end relay
            //SendRelay(zsocket, RelayType.UIRecieve, actual.getBody(), date);
            SendRelay(zsocket, RelayType.UIRecieve, this.hashOfLastMessage, "33434", date);
        }
        catch(Exception e)
        {

        }
    }

    public Core() {
        this.date = new Date();
        gson = new Gson();

        try
        {
            CryptoUtil.init();
            keyPair = RSAEncryptUtil.generateKey();
            publicKey = keyPair.getPublic();
            privateKey = keyPair.getPrivate();

            this.hashOfLastMessage = PasswordUtil.hash("");
        }
        catch(Exception e)
        {

        }
    }

    public void Start() {

        try (ZContext context = new ZContext()) {
            // Socket to talk to clients
            zsocket = context.createSocket(ZMQ.REP);
            zsocket.bind("tcp://*:5555");

            while (!Thread.currentThread().isInterrupted()) {
                // Block until a message is received
                byte[] raw = zsocket.recv(0);
                Relay xRelay = ReadRelay(raw);
                System.out.println("JVReceived: " + xRelay);

                RelayType relayType = xRelay.getType();
                // switch to enum
                switch(relayType)
                {
                    default:
                        break;
                    case CRYPTORequestConnect:
                        ConnectToServer();
                        break;
                    case CRYPTORequestDisconnect:
                        DisconnectFromServer(); 
                        break;
                    case CRYPTOSend:
                        SendMessage(xRelay);
                        break;
                    case CRYPTORequestRecieve:
                        FakeRcMessage();
                        break;
                }
            }
        }
    }
}
