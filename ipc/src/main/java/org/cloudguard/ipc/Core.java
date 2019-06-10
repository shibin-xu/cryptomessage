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
    private Date date;
    private String cookieToken;
    private Queue<String> zbuffer;
    private ZMQ.Socket zsocket;

    private PublicKey publicKey;
    private PrivateKey privateKey;

    private Map<String, String> contacts;
    private Map<String, String> lastHashes;
    private Map<String, Integer> nextIdentifiers;
    private Map<String, List<Envelope>> envelopeMap;
    private Map<String, List<Speech>> speechMap;

    private void ConnectWithKeys(String publicKeyPath, String privateKeyPath) {

        try {
            publicKey = CoreKeyUtil.GetPublicKey(publicKeyPath);
            privateKey = CoreKeyUtil.GetPrivateKey(privateKeyPath);
        } catch (Exception e) {
            System.out.println("failed to read key = " + e);
            publicKey = null;
            privateKey = null;
        }

        if (publicKey == null || privateKey == null) {
            try {
                KeyPair keyPair = RSAEncryptUtil.generateKey();
                publicKey = keyPair.getPublic();
                privateKey = keyPair.getPrivate();
                CoreKeyUtil.SavePublicKey(publicKey, publicKeyPath);
                CoreKeyUtil.SavePrivateKey(privateKey, privateKeyPath);
            } catch (Exception e) {
                System.out.println("failed to save key = " + e);
                SendRelay(zsocket, RelayType.UIResultForConnect, "False", "False", date);
            }
        }

        String publicKeyString = getKeyAsString(publicKey);
        String n1 = "";
        LoginPrepareResponse loginPrepareResponse = null;
        try {
            n1 = generateCookie();
            SendRelay(zsocket, RelayType.UIPublicKey, publicKeyString, "pubkey", date);

            LoginPrepareRequest loginPrepareRequest = new LoginPrepareRequest(publicKeyString, n1);
            Request request = new Request(loginPrepareRequest.getClass().getName(), gson.toJson(loginPrepareRequest));
            Response response = getResponse(request);
            loginPrepareResponse = gson.fromJson(response.getJson(), LoginPrepareResponse.class);
            SendRelay(zsocket, RelayType.UISecurity, "nonce", loginPrepareResponse.getNonce(), date);
        } catch (Exception e) {
            System.out.println("failed to login prepare = " + e);
        }

        try {
            String n2 = loginPrepareResponse.getNonce();
            String n1n2 = n1 + n2;
            String signature = RSAEncryptUtil.sign(n1n2, privateKey);
            LoginFinishRequest loginFinishRequest = new LoginFinishRequest(publicKeyString, signature);
            Request request = new Request(loginFinishRequest.getClass().getName(), gson.toJson(loginFinishRequest));
            Response response = getResponse(request);
            LoginFinishResponse loginFinishResponse = gson.fromJson(response.getJson(), LoginFinishResponse.class);
            cookieToken = loginFinishResponse.getToken();

            SendRelay(zsocket, RelayType.UIResultForConnect, "True", "True", date);
            
            AddContactString(publicKeyString, "self");
        } catch (Exception e) {
            System.out.println("failed to login finish = " + e);
            SendRelay(zsocket, RelayType.UIResultForConnect, "True", "False", date);
        }
    }

    private void DisconnectFromServer() {
        //FIXME ?
        SendRelay(zsocket, RelayType.UIResultForDisconnect, "-", "-", date);

    }

    private void Tick(String contactKey) {

        GetResponse getResponse = null;
        try {
            if (cookieToken != null) {
                GetRequest getRequest = new GetRequest(cookieToken);
                Request request = new Request(getRequest.getClass().getName(), gson.toJson(getRequest));
                Response response = getResponse(request);
                getResponse = gson.fromJson(response.getJson(), GetResponse.class);
            }
        } catch (Exception e) {

            System.out.println("failed get = " + e);
        }

        try {
            if (getResponse != null) {
                System.out.println("getResponse = " + getResponse);
                System.out.println();
                CoreMessageUtil.readMessage(getResponse.getEnvelopes(), privateKey, envelopeMap, speechMap);
                System.out.println("update archive");
                System.out.println();
                GetContactArchive(contactKey);
            }
        } catch (Exception e) {

            System.out.println("failed read = " + e);
        }

    }


    private void AddContactString(String publicKeyString, String alias) {
        try {
            String result = contacts.put(publicKeyString, alias);
            if (result != null) {
                SendRelay(zsocket, RelayType.UIResultForRenameContact, "True", alias, date);
            } else {
                SendRelay(zsocket, RelayType.UIResultForAddContact, "True", alias, date);
            }
        } catch (Exception e) {
            System.out.println("add contact exception = " + e);
        }
        GetAllContact();
    }
    private void AddContactFile(String publicKeyFile, String alias) {
        try {
            PublicKey contactKey = CoreKeyUtil.GetPublicKey(publicKeyFile);
            String contactKeyString = getKeyAsString(contactKey);
            String result = contacts.put(contactKeyString, alias);
            if (result != null) {
                SendRelay(zsocket, RelayType.UIResultForRenameContact, "True", alias, date);
            } else {
                SendRelay(zsocket, RelayType.UIResultForAddContact, "True", alias, date);
            }
        } catch (Exception e) {
            System.out.println("add contact exception = " + e);
        }
        GetAllContact();
    }

    private void RemoveContact(String contactKeyString, String alias) {
        try {
            boolean removed = contacts.remove(contactKeyString, alias);
            if (removed) {
                SendRelay(zsocket, RelayType.UIResultForRemoveContact, "True", alias, date);
            } else {
                SendRelay(zsocket, RelayType.UIResultForRemoveContact, "False", alias, date);
            }
        } catch (Exception e) {
            System.out.println("add contact exception = " + e);
        }
    }

    private void RenameContact(String contactKeyString, String alias) {
        try {
            String result = contacts.put(contactKeyString, alias);
            if (result != null) {
                SendRelay(zsocket, RelayType.UIResultForRenameContact, "True", alias, date);
            } else {
                SendRelay(zsocket, RelayType.UIResultForAddContact, "True", alias, date);
            }
            
        } catch (Exception e) {
            System.out.println("add contact exception = " + e);
        }
    }

    private void GetAllContact() {
        try {
            for (Map.Entry<String, String> entry : contacts.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                SendRelay(zsocket, RelayType.UIResultForContact, key, value, date);
            }
        } catch (Exception e) {
            System.out.println("get contact exception = " + e);
        }
    }

    private void GetContactArchive(String contactKey) {
        try {
            Integer clientSendCount = 0;
            if(speechMap.containsKey(contactKey))
            {
                List<Speech> speechList = speechMap.get(contactKey);
                for (Speech speech : speechList) {
                    boolean isReceiveByContact = speech.getRecipientKey().equals(contactKey);
                    if (isReceiveByContact) {
                        clientSendCount++;
                    }
                    SendRelay(zsocket, RelayType.UISpeechUpdate, gson.toJson(speech), contactKey, date);
                    
                }
            }
            if(clientSendCount == 0) {
                SendRelay(zsocket, RelayType.UISpeechUpdate, "", contactKey, date);
            }
            Integer nextIdentifier = clientSendCount;
            nextIdentifiers.put(contactKey, 100 + nextIdentifier);
            SendRelay(zsocket, RelayType.UISpeechNextIdentifier, Integer.toString(nextIdentifier), contactKey, date);
        } catch (Exception e) {

            System.out.println("archive exception = " + e);
        }

    }

    private void SendSpeech(String sendText, String destinationKeyString) {
        
        String destinationAlias = "unknown";
        String publicKeyString = getKeyAsString(publicKey);
        try {
            
            if (!contacts.containsKey(destinationKeyString)) {
                contacts.put(destinationKeyString, destinationAlias);
            }
            destinationAlias = this.contacts.get(destinationKeyString);
        } catch (Exception e) {
            System.out.println("contact = " + e);
        }

        try {
            PublicKey recipient = CoreKeyUtil.GetPublicKeyFromText(destinationKeyString); 
            if (!lastHashes.containsKey(destinationKeyString)) {
                lastHashes.put(destinationKeyString, PasswordUtil.hash(""));
            }
            String hashOfLastMessage = lastHashes.get(destinationKeyString);
            Message message = CoreMessageUtil.makeMessage(recipient, sendText, hashOfLastMessage, publicKeyString);
            SendResponse sendResponse = CoreMessageUtil.sendMessage(recipient, privateKey, message);
            if (sendResponse == null || !sendResponse.isSuccess()) {
                SendRelay(zsocket, RelayType.UIResultForSpeechSend, sendText, "False", this.date);
                return;
            }

        } catch (Exception e) {
            System.out.println("Failed SendSpeech = " + e);
            SendRelay(zsocket, RelayType.UIResultForSpeechSend, sendText, "False", this.date);
            return;
        }

        try {
            SendRelay(zsocket, RelayType.UIResultForSpeechSend, sendText, "True", this.date);
            if (!nextIdentifiers.containsKey(destinationKeyString)) {
                nextIdentifiers.put(destinationKeyString, 100);
            }
            Integer identifier = nextIdentifiers.get(destinationKeyString);
            Speech speech = new Speech(identifier, publicKeyString, destinationAlias, sendText, date.getTime());
            speech.Destination(destinationKeyString);
            speech.Verify(true, true);
            if(!speechMap.containsKey(destinationKeyString)) {
                List<Speech> emptyList = new ArrayList<>();
                speechMap.put(destinationKeyString, emptyList);
            }
            List<Speech> speechList = speechMap.get(destinationKeyString);
            speechList.add(speech);

            GetContactArchive(destinationKeyString);

        } catch (Exception e) {
            System.out.println("Failed Speech = " + e);
        }
    }

    public Core() {
        gson = new Gson();
        date = new Date();

        cookieToken = null;
        zbuffer = new ArrayDeque<String>();

        lastHashes = new HashMap<String, String>();
        contacts = new HashMap<String, String>();
        nextIdentifiers = new HashMap<String, Integer>();
        envelopeMap = new HashMap<String, List<Envelope>>();
        speechMap = new HashMap<String, List<Speech>>();

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
                case CRYPTOTick:
                    Tick(inputRelay.getPrimaryData());
                    break;
                case CRYPTOConnectWithKeys:
                    ConnectWithKeys(inputRelay.getPrimaryData(), inputRelay.getSecondaryData());
                    break;
                case CRYPTODisconnectFromServer:
                    DisconnectFromServer();
                    break;
                case CRYPTOAddContactString:
                    AddContactString(inputRelay.getPrimaryData(), inputRelay.getSecondaryData());
                    break;
                case CRYPTOAddContactFile:
                    AddContactFile(inputRelay.getPrimaryData(), inputRelay.getSecondaryData());
                    break;
                case CRYPTORemoveContact:
                    RemoveContact(inputRelay.getPrimaryData(), inputRelay.getSecondaryData());
                    break;
                case CRYPTORenameContact:
                    RenameContact(inputRelay.getPrimaryData(), inputRelay.getSecondaryData());
                    break;
                case CRYPTOGetContactArchive:
                    GetContactArchive(inputRelay.getPrimaryData());
                    break;
                case CRYPTOSend:
                    SendSpeech(inputRelay.getPrimaryData(), inputRelay.getSecondaryData());
                    break;
                case CRYPTOFakeFill:
                    CoreFakeUtil.FakeFill(contacts, speechMap, publicKey);
                    GetAllContact();
                    break;
                case CRYPTOFakeReceive:
                    String senderKey = inputRelay.getPrimaryData();
                    Speech speech = CoreFakeUtil.FakeRecvSpeech(senderKey, 
                        inputRelay.getSecondaryData(), publicKey, contacts, date);
                    if (speech != null) {
                        if(!speechMap.containsKey(senderKey)) {
                            List<Speech> emptyList = new ArrayList<>();
                            speechMap.put(senderKey, emptyList);
                        }
                        List<Speech> speechList = speechMap.get(senderKey);
                        speechList.add(speech);
                        SendRelay(zsocket, RelayType.UISpeechUpdate, gson.toJson(speech), senderKey, date);
                    }
                    break;
                }
                while (zbuffer.peek() != null) {
                    String serialized = zbuffer.remove();
                    if (zbuffer.peek() != null) {
                        zsocket.sendMore(serialized.getBytes(ZMQ.CHARSET));
                    } else {
                        zsocket.send(serialized.getBytes(ZMQ.CHARSET), 0);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("zsocket exception = " + e);
        }
    }
    private Relay ReadRelay(byte[] raw) {

        Relay r = null;
        try{
            String rawString = new String(raw, ZMQ.CHARSET);
            r = gson.fromJson(rawString, Relay.class);
        } catch (Exception e) {
                
            System.out.println("ReadRelay exception = " + e);
        }
        return r;
    }

    private void SendRelay(ZMQ.Socket zsocket, RelayType type, String primary, String secondary, Date date) {

        try {
            Relay relay = new Relay(type, primary, secondary, 2);
            String serialized = gson.toJson(relay);
            System.out.println("ToUI: " + relay.getType());
            System.out.println("....: " + primary);
            System.out.println(".  .: " + secondary);
            zbuffer.add(serialized);
        } catch (Exception e) { 
            System.out.println("SendRelay exception = " + e);
        }
    }

}
