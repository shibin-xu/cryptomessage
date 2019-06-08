package org.cloudguard.ipc;

import com.google.gson.Gson;
import org.cloudguard.commons.*;
import org.cloudguard.crypto.CryptoUtil;
import org.cloudguard.crypto.PasswordUtil;
import org.cloudguard.crypto.RSAEncryptUtil;

import java.security.*;
import java.util.*;

import static org.cloudguard.commons.ClientUtil.getResponse;
import static org.cloudguard.crypto.PasswordUtil.generateCookie;
import static org.cloudguard.crypto.RSAEncryptUtil.getKeyAsString;

import org.zeromq.ZMQ;
import org.zeromq.ZContext;

public class Core {

    private Gson gson;
    private KeyPair keyPair;
    private PublicKey publicKey;
    private PrivateKey privateKey;
    private ZMQ.Socket zsocket;
    private Queue<String> zbuffer;
    private Date date;

    private Map<String, String> contacts;
    private Map<String, String> lastHashes;
    private List<Speech> archive;


    private Relay ReadRelay(byte[] raw) {

        String rawString = new String(raw, ZMQ.CHARSET);
        Relay r = gson.fromJson(rawString, Relay.class);
        return r;
    }

    private void SendRelay(ZMQ.Socket zsocket, RelayType type, String payload, String sender, Date date) {

        Relay relay = new Relay(type, payload, sender, 2);
        String serialized = gson.toJson(relay);
        System.out.println("BufferToUI: " + serialized);
        zbuffer.add(serialized);
    }

    private void DisconnectFromServer() {
        // internet socket stuff
        // ..

        SendRelay(zsocket, RelayType.UIResultForDisconnect, "-", "-", date);

    }


    private void ConnectWithKeys(Relay in) {

        gson = new Gson();
        CryptoUtil.init();

        try {
            publicKey = CoreKeyUtil.GetPublicKey(in.getPrimaryData());
            privateKey = CoreKeyUtil.GetPrivateKey(in.getSecondaryData());
        } catch(Exception e) {
            System.out.println("e1 = " + e);
            publicKey = null;
            privateKey = null;
        }
        
        if(publicKey == null || privateKey == null) {
            try {
                KeyPair keyPair = RSAEncryptUtil.generateKey();
                publicKey = keyPair.getPublic();
                privateKey = keyPair.getPrivate();
                CoreKeyUtil.SavePublicKey(publicKey, in.getPrimaryData());
                CoreKeyUtil.SavePrivateKey(privateKey, in.getSecondaryData());
            } catch(Exception e) {
                System.out.println("e2 = " + e);
                SendRelay(zsocket, RelayType.UIResultForConnect, "False", "False", date);
            }
        }
        String n1 = "";
        LoginPrepareResponse loginPrepareResponse= null; 
        try {
            n1 = generateCookie();
            String publicKeyString = getKeyAsString(publicKey);
            SendRelay(zsocket, RelayType.UIPublicKey, publicKeyString,"pubkey", date);

            LoginPrepareRequest loginPrepareRequest = new LoginPrepareRequest(publicKeyString, n1);
            Request request = new Request(loginPrepareRequest.getClass().getName(), gson.toJson(loginPrepareRequest));
            Response response = getResponse(request);
            loginPrepareResponse = gson.fromJson(response.getJson(), LoginPrepareResponse.class);
            SendRelay(zsocket, RelayType.UISecurity, "nonce", loginPrepareResponse.getNonce(), date);
        } catch (Exception e) {
            System.out.println("e3 = " + e);
        }

        try {
            String publicKeyString = getKeyAsString(publicKey);
            String n2 = loginPrepareResponse.getNonce();
            String n1n2 = n1 + n2;
            String signature = RSAEncryptUtil.sign(n1n2, privateKey);
            LoginFinishRequest loginFinishRequest = new LoginFinishRequest(publicKeyString, signature);
            Request request = new Request(loginFinishRequest.getClass().getName(), gson.toJson(loginFinishRequest));
            Response response = getResponse(request);
            LoginFinishResponse loginFinishResponse = gson.fromJson(response.getJson(), LoginFinishResponse.class);
            
            System.out.println("loginFinish = " + loginFinishResponse);
            System.out.println();


            SendRelay(zsocket, RelayType.UIResultForConnect, "True", "True", date);
        } catch(Exception e) {
            System.out.println("e4 = " + e);
            SendRelay(zsocket, RelayType.UIResultForConnect, "True", "False", date);
        }
    }

    private void AddContact(Relay in) {
        String publicKeyString = in.getPrimaryData();
        String alias = in.getSecondaryData();
        String result = contacts.put(publicKeyString, alias);
        if(result != null) {
            SendRelay(zsocket, RelayType.UIResultForAddContact, "True", alias, date);
        } else {
            SendRelay(zsocket, RelayType.UIResultForAddContact, "False", alias, date);
        }
    }

    private void RemoveContact(Relay in) {
        String publicKeyString = in.getPrimaryData();
        String alias = in.getSecondaryData();
        
        boolean removed = contacts.remove(publicKeyString, alias);
        if(removed) {
            SendRelay(zsocket, RelayType.UIResultForRemoveContact, "True", alias, date);
        } else {
            SendRelay(zsocket, RelayType.UIResultForRemoveContact, "False", alias, date);
        }
    }

    private void RenameContact(Relay in) {
        String publicKeyString = in.getPrimaryData();
        String newAlias = in.getSecondaryData();
        String result = contacts.put(publicKeyString, newAlias);
        if(result != null) {
            SendRelay(zsocket, RelayType.UIResultForRenameContact, "True", newAlias, date);
        } else {
            SendRelay(zsocket, RelayType.UIResultForAddContact, "False", newAlias, date);
        }
    }

    private void GetAllContact() {
        try {
            for(Map.Entry<String, String> entry : contacts.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                SendRelay(zsocket, RelayType.UIResultForContact, key, value, date);
            }
        } catch(Exception e) {
            System.out.println("get contact exception = " + e);
        }
    }

    private void GetContactArchive(Relay in) {
        try {
            String contactKey = in.getPrimaryData();
            for(Speech speech : archive) {
                String senderKey =  speech.getSenderKey();
                String recipientKey = speech.getRecipientKey();
                if (senderKey.equals(contactKey) || recipientKey.equals(contactKey)) {
                    String serialized = gson.toJson(speech);
                    SendRelay(zsocket, RelayType.UIResultForArchive, serialized, contactKey, date);
                }
            }
            SendRelay(zsocket, RelayType.UIResultForArchive, "", contactKey, date);
        } catch(Exception e) {
                
            System.out.println("archive exception = " + e);
        }
        
    }

    private void SendSpeech(Relay relay) {

        String sendText = relay.getPrimaryData();
        String senderKeyString = relay.getSecondaryData();
        try {
            String hashOfLastMessage = "fix";
            if(hashOfLastMessage == null)
            {
                hashOfLastMessage = PasswordUtil.hash("");
            }
            Message message = CoreMessageUtil.makeMessage(publicKey, sendText, hashOfLastMessage, senderKeyString);
            SendResponse sendResponse = CoreMessageUtil.sendMessage(publicKey, privateKey, message);
            if(sendResponse == null)
            {
                SendRelay(zsocket, RelayType.UIResultForSpeechSend, sendText, "False", this.date);
                return;
            }
            
        } catch (Exception e) {
            System.out.println("e5 = " + e);
            SendRelay(zsocket, RelayType.UIResultForSpeechSend, sendText, "False", this.date);
            return;
        }
        SendRelay(zsocket, RelayType.UIResultForSpeechSend, sendText, "True", this.date);
    }

    public Core() {
        date = new Date();
        gson = new Gson();
        zbuffer = new ArrayDeque<String>();

        lastHashes = new HashMap<String, String>();
        contacts = new HashMap<String, String>();
        archive = new ArrayList<Speech>();

        try {
            CryptoUtil.init();
        } catch (Exception e) {
            System.out.println("CryptoUtil exception = " + e);
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

                switch (relayType) {
                default:
                    break;
                case CRYPTOConnectWithKeys:
                    ConnectWithKeys(inputRelay);
                    break;
                case CRYPTODisconnectFromServer:
                    DisconnectFromServer();
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
                    SendSpeech(inputRelay);
                    break;
                case CRYPTOFakeFill:
                    CoreFakeUtil.FakeFill(contacts, archive, publicKey);
                    GetAllContact();
                    break;
                case CRYPTOFakeReceive:
                    Speech speech = CoreFakeUtil.FakeRecvSpeech(inputRelay.getPrimaryData(), 
                        inputRelay.getSecondaryData(), publicKey, contacts, date);
                    if(speech != null) {
                        archive.add(speech);
                        String serialized = gson.toJson(speech);
                        SendRelay(zsocket, RelayType.UISpeechReceive, serialized, "", date);
                    }
                    break;
                }
                while(zbuffer.peek() != null) {
                    String serialized = zbuffer.remove();
                    if(zbuffer.peek() != null)
                    {
                        zsocket.sendMore(serialized.getBytes(ZMQ.CHARSET));   
                    }
                    else
                    {
                        zsocket.send(serialized.getBytes(ZMQ.CHARSET), 0);
                    }
                }
            }
        }
        catch(Exception e)
        {

            System.out.println("e9 = " + e);
        }
    }
}
