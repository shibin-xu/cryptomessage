package org.cloudguard.ipc;

import com.google.gson.Gson;

import org.cloudguard.commons.Response;
import org.cloudguard.commons.ClientUtil;
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

    private void LoginNew(Relay in) {
        String email = in.getPrimaryData();
        String password = in.getSecondaryData();
        try {
            Response response = ClientUtil.register(email, "first", "last", password);
            if(response != null) {
                SendRelay(zsocket, RelayType.UIResultForNewAccount, response.getClassName(), response.getJson(), date);
            }
        } catch(Exception e) {

            System.out.println("LoginNew: " + e);
        }
    }

    private void LoginExisting(Relay in) {
        String email = in.getPrimaryData();
        String password = in.getSecondaryData();
        try {
            Response response = ClientUtil.login(email, password);
            if(response != null) {
                SendRelay(zsocket, RelayType.UIResultForExistingLogin, response.getClassName(), response.getJson(), date);
            }
        }
        catch(Exception e) {

            System.out.println("LoginExisting: " + e);
        }
    }

    private void DisconnectFromServer() {
        // internet socket stuff
        // ..

        SendRelay(zsocket, RelayType.UIResultForDisconnect, "-", "-", date);
                        
    }

    private void LoadPublicKeys(Relay in) {
        String filepath = in.getPrimaryData();
        // something with filepath
        SendRelay(zsocket, RelayType.UIResultForKeyPath, "True", filepath, date);
    }

    

    private void AddContact(Relay in) {
        String publicKey = in.getPrimaryData();
        String alias = in.getSecondaryData();
        SendRelay(zsocket, RelayType.UIResultForAddContact, "True", alias, date);
    }

    private void RemoveContact(Relay in) {
        String publicKey = in.getPrimaryData();
        String alias = in.getSecondaryData();
        SendRelay(zsocket, RelayType.UIResultForRemoveContact, "False", alias, date);
    }    
    private void RenameContact(Relay in) {
        String oldAlias = in.getPrimaryData();
        String nextAlias = in.getSecondaryData();
        SendRelay(zsocket, RelayType.UIResultForRenameContact, "False", oldAlias, date);
    }
    private void GetAllContact() {
        SendRelay(zsocket, RelayType.UIResultForAllContact, "bob,eve", "-", date);
    }
    private void GetContactArchive(Relay in) {
        String publicKey = in.getPrimaryData();
        String alias = in.getSecondaryData();
        SendRelay(zsocket, RelayType.UIResultForContactArchive, "a,b,c,d,e", alias, date);
    }

    private void SendMessage(Relay relay) {
        
        try {
         
            Message message = new Message(relay.getPrimaryData(), this.hashOfLastMessage, date.getTime());
            String text = gson.toJson(message);
            String proof = RSAEncryptUtil.sign(text, this.privateKey);
            Envelope envelope = new Envelope(message, proof);
            
            this.hashOfLastMessage = PasswordUtil.hash(text);

            // internet socket stuff
            // ..

            SendRelay(zsocket, RelayType.UIResultForMessageSend, relay.getPrimaryData(), "0", this.date);   
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
            String origin = "abdf23";

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
                    case CRYPTOLoginNewAccount:
                        LoginNew(inputRelay); 
                        break;
                    case CRYPTOLoginExistingAccount:
                        LoginExisting(inputRelay); 
                        break;
                    case CRYPTODisconnectFromServer:
                        DisconnectFromServer();
                        break;
                    case CRYPTOSetFilePathOfKey:
                        LoadPublicKeys(inputRelay); 
                        break;
                    case CRYPTOAddContact:
                        AddContact(inputRelay);
                        break;
                    case CRYPTORemoveContact:
                        RemoveContact(inputRelay);
                        break;
                    case CRYPTORenameContact:
                        RenameContact(inputRelay);
                        break;
                    case CRYPTOGetAllContact:
                        GetAllContact();
                        break;
                    case CRYPTOGetContactArchive:
                        GetContactArchive(inputRelay);
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
