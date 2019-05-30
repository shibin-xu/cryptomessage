package org.cloudguard.ipc;

import com.google.gson.Gson;
import org.cloudguard.crypto.CryptoUtil;
import org.cloudguard.crypto.PasswordUtil;
import org.cloudguard.crypto.RSAEncryptUtil;

import java.security.*;
import java.util.Date;

import org.zeromq.ZMQ;
import org.zeromq.ZContext;

public class Main {
    
    protected static Gson gson;
    protected static KeyPair keyPair;
    protected static PublicKey publicKey;
    protected static PrivateKey privateKey;

    public static Relay ReadRelay(byte[] raw) {
        
        String rawString = new String(raw, ZMQ.CHARSET);
        Relay r = gson.fromJson(rawString, Relay.class);
        return r;
    }

    public static void SendRelay(ZMQ.Socket zsocket, RelayType type, String payload, Date date) {
        
        Relay relay = new Relay(type, payload, 2);
        String serialized = gson.toJson(relay);
        System.out.println("CRYPTOSend: " + serialized);
        zsocket.send(serialized.getBytes(ZMQ.CHARSET), 0);
    }

    public static void main(String[] args) throws Exception {
        Date date = new Date();

        System.out.println(date);
        System.out.println(date.getTime());

        System.out.println(new Date(date.getTime()));

        gson = new Gson();

        CryptoUtil.init();
        keyPair = RSAEncryptUtil.generateKey();
        publicKey = keyPair.getPublic();
        privateKey = keyPair.getPrivate();

        String hashOfLastMessage = PasswordUtil.hash("");

        try (ZContext context = new ZContext()) {
            // Socket to talk to clients
            ZMQ.Socket zsocket = context.createSocket(ZMQ.REP);
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
                    
                        // internet socket stuff
                        // ..

                        // front end relay
                        SendRelay(zsocket, RelayType.UIConfirmConnect, "", date);
                        break;
                    case CRYPTORequestDisconnect:
                    
                        // internet socket stuff
                        // ..

                        // front end relay
                        SendRelay(zsocket, RelayType.UIConfirmDisconnect, "", date);
                        break;
                    case CRYPTOSend:
                        Message message = new Message(xRelay.getContent(), hashOfLastMessage, date.getTime());
                        String text = gson.toJson(message);
                        String proof = RSAEncryptUtil.sign(text, privateKey);
                        Envelope envelope = new Envelope(message, proof);
                        
                        hashOfLastMessage = PasswordUtil.hash(text);

                        // internet socket stuff
                        // ..

                        // front end relay
                        SendRelay(zsocket, RelayType.UIConfirmSend, xRelay.getContent(), date);
                        break;
                    case CRYPTORequestRecieve:
                        // TODO, change this to  automatic whenever a packet comes in

                        String body = "This is a test.";
                        Message expected = new Message(body, hashOfLastMessage, date.getTime());
                        String serialized = gson.toJson(expected);
                        Message actual = gson.fromJson(serialized, Message.class);

                        // front end relay
                        //SendRelay(zsocket, RelayType.UIRecieve, actual.getBody(), date);
                        SendRelay(zsocket, RelayType.UIRecieve, hashOfLastMessage, date);
                        break;
                }
            }
        }
    }
}
