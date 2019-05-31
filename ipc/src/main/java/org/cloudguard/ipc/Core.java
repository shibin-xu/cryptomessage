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
        System.out.println("SendToUI: " + serialized);
        zsocket.send(serialized.getBytes(ZMQ.CHARSET), 0);
    }

    private void ConnectToServer() {
        // internet socket stuff
        // ..

        SendRelay(zsocket, RelayType.UIResultForConnect, "4.4.4.255", "0", date);
                        
    }

    private void DisconnectFromServer() {
        // internet socket stuff
        // ..

        SendRelay(zsocket, RelayType.UIResultForDisconnect, "0.1.2.3", "0", date);
                        
    }

    private void LoadPublicKeys(Relay in) {
        SendRelay(zsocket, RelayType.UIResultForKeyPath, "True", "0", date);
    }

    
    private void LoginNew(Relay in) {
        SendRelay(zsocket, RelayType.UIResultForNewAccount, "True", "0", date);
    }

    
    private void LoginExisting(Relay in) {
        SendRelay(zsocket, RelayType.UIResultForExistingLogin, "True", "0", date);
    }

    private void AddUser(Relay in) {
        SendRelay(zsocket, RelayType.UIResultForAddUser, "True", "0", date);
    }

    private void RemoveUser(Relay in) {
        SendRelay(zsocket, RelayType.UIResultForRemoveUser, "False", "0", date);
    }
    private void GetAllUser() {
        SendRelay(zsocket, RelayType.UIResultForAllUser, "bob,eve", "0", date);
    }
    private void GetUserArchive(Relay in) {
        SendRelay(zsocket, RelayType.UIResultForUserArchive, "a,b,c,d,e", "0", date);
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

            SendRelay(zsocket, RelayType.UIResultForMessageSend, relay.getContent(), "0", this.date);   
        }
        catch(Exception e)
        {

        }

    }

    private void FakeRcMessage() {
        
        try
        {
            String body = "This is a test.";
            Message expected = new Message(body, this.hashOfLastMessage, date.getTime());
            String serialized = gson.toJson(expected);
            Message actual = gson.fromJson(serialized, Message.class);
            String origin = "3434";

            SendRelay(zsocket, RelayType.UIMessageReceive, actual.getBody(), origin, date);
        }
        catch(Exception e)
        {

        }
    }

    public Core() {
        date = new Date();
        gson = new Gson();

        try
        {
            CryptoUtil.init();
            keyPair = RSAEncryptUtil.generateKey();
            publicKey = keyPair.getPublic();
            privateKey = keyPair.getPrivate();

            hashOfLastMessage = PasswordUtil.hash("");
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
                Relay inputRelay = ReadRelay(raw);
                System.out.println("ReceivedFromUI: " + inputRelay);

                RelayType relayType = inputRelay.getType();
                
                switch(relayType)
                {
                    default:
                        break;
                    case CRYPTOOpenConnectionToServer:
                        ConnectToServer();
                        break;
                    case CRYPTODisconnectFromServer:
                        DisconnectFromServer();
                        break;
                    case CRYPTOLoginNewAccount:
                        LoginNew(inputRelay); 
                        break;
                    case CRYPTOLoginExistingAccount:
                        LoginExisting(inputRelay); 
                        break;
                    case CRYPTOSetFilePathOfKey:
                        LoadPublicKeys(inputRelay); 
                        break;
                    case CRYPTOAddUser:
                        AddUser(inputRelay);
                        break;
                    case CRYPTORemoveUser:
                        RemoveUser(inputRelay);
                        break;
                    case CRYPTOGetAllUser:
                        GetAllUser();
                        break;
                    case CRYPTOGetUserArchive:
                        GetUserArchive(inputRelay);
                        break;
                    case CRYPTOSend:
                        SendMessage(inputRelay);
                        break;
                    case CRYPTOFakeReceive:
                        FakeRcMessage();
                        break;
                }
            }
        }
    }
}
