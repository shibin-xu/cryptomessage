package org.cloudguard.ipc;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.cloudguard.commons.*;
import org.cloudguard.crypto.CryptoUtil;
import org.cloudguard.crypto.PasswordUtil;
import org.cloudguard.crypto.RSAEncryptUtil;

import java.security.*;
import java.time.Instant;
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
                
                ArrayList<Speech> speechBuffer = CoreMessageUtil.readMessage(getResponse.getEnvelopes(), privateKey, envelopeMap);
                for(Speech speech : speechBuffer) {
                    List<Speech> speechList = GetSpeechList(speech.getSenderKey());
                    AddToSpeechList(speechList, speech);
                }
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
        
        System.out.println("RemoveContact = " + contactKeyString);
        try {
            boolean removed = contacts.remove(contactKeyString, alias);
            if (removed) {
                SendRelay(zsocket, RelayType.UIResultForRemoveContact, "True", alias, date);
            } else {
                SendRelay(zsocket, RelayType.UIResultForRemoveContact, "False", alias, date);
            }
        } catch (Exception e) {
            System.out.println("rem contact exception = " + e);
        }
        GetAllContact();
    }

    private void RenameContact(String contactKeyString, String alias) {
        
        System.out.println("RemoveContact = " + contactKeyString);
        try {
            String result = contacts.put(contactKeyString, alias);
            if (result != null) {
                SendRelay(zsocket, RelayType.UIResultForRenameContact, "True", alias, date);
            } else {
                SendRelay(zsocket, RelayType.UIResultForAddContact, "True", alias, date);
            }
            
        } catch (Exception e) {
            System.out.println("ren contact exception = " + e);
        }
        GetAllContact();
    }

    private void GetAllContact() {
        try {
            for (Map.Entry<String, String> entry : contacts.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                SendRelay(zsocket, RelayType.UIResultFoundContact, key, value, date);
            }
        } catch (Exception e) {
            System.out.println("get contact exception = " + e);
        }
    }

    private void GetContactArchive(String contactKey) {
        try {
            Integer messageCount = 0;
            if(speechMap.containsKey(contactKey))
            {
                List<Speech> speechList = speechMap.get(contactKey);
                for (Speech speech : speechList) {
                    messageCount++;
                    SendRelay(zsocket, RelayType.UISpeechUpdate, gson.toJson(speech), contactKey, date);
                }
            }
            if(messageCount == 0) {
                SendRelay(zsocket, RelayType.UISpeechUpdate, "", contactKey, date);
            }
            
        } catch (Exception e) {

            System.out.println("archive exception = " + e);
        }

    }

    private List<Speech> GetSpeechList(String keyString) {
        if(!contacts.containsKey(keyString)) {
            String alias = keyString.substring(44,54);
            System.out.println("put alias = " + alias);
            contacts.put(keyString, alias);
            GetAllContact();
        }
        if(!speechMap.containsKey(keyString)) {
            List<Speech> emptyList = new ArrayList<>();
            speechMap.put(keyString, emptyList);
        }
        return speechMap.get(keyString);
    }

    private void AddToSpeechList(List<Speech> speechList, Speech speech) {
        if(speechList == null) {
            return;
        }
        for (Iterator<Speech> it = speechList.iterator(); it.hasNext(); ) { 
            Speech s = it.next();
            if (s.getIdentifier() == speech.getIdentifier()) { 
                it.remove(); 
            } 
        }
        speechList.add(speech);
    }

    private void AddLocalSpeech(String sendText, String identifier, String destinationKeyString) {
        try {
            String publicKeyString = getKeyAsString(publicKey);
            Speech speech = new Speech(identifier, publicKeyString, destinationKeyString, sendText, true, true, date.getTime());

            System.out.println("add local "+destinationKeyString);
            List<Speech> speechList = GetSpeechList(destinationKeyString);
            AddToSpeechList(speechList, speech);

            GetContactArchive(destinationKeyString);
        } catch (Exception e) {
            System.out.println("Failed AddLocalSpeech = " + e);
            SendRelay(zsocket, RelayType.UIResultForSpeechSend, sendText, "False", this.date);
            return;
        }
    }

    private void AddRemoteSpeech(String decryptedBody, 
        String identifier, String senderKeyString, Boolean hashMatched, Boolean signatureVerified) {
        
        try{
            String publicKeyString = getKeyAsString(publicKey);
            Speech speech = new Speech(identifier, senderKeyString, publicKeyString, 
                decryptedBody, hashMatched, signatureVerified, date.getTime());

            List<Speech> speechList = GetSpeechList(senderKeyString);
            AddToSpeechList(speechList, speech);
            

            GetContactArchive(senderKeyString);

        } catch (Exception e) {
            System.out.println("Failed RemoteSpeech = " + e);
            SendRelay(zsocket, RelayType.UIResultForSpeechSend, decryptedBody, "False", this.date);
            return;
        }
    }
    private void Save(String saveFilePath) {
        Blob blob = new Blob(contacts, lastHashes, envelopeMap, speechMap);
        Gson gsonBuild = new GsonBuilder().setPrettyPrinting().create();
        String serialized = gsonBuild.toJson(blob);
        try {
            CoreSaveLoadUtil.SaveToFile(saveFilePath, serialized);
            SendRelay(zsocket, RelayType.UIResultForSaveLoad, "True", "False", date);
        } catch (Exception e) {
            System.out.println("Failed save = " + e);
            SendRelay(zsocket, RelayType.UIResultForSaveLoad, "False", "False", this.date);
            return;
        } 
    }
    private void Load(String loadFilePath) {
        try {
            String serialized = CoreSaveLoadUtil.LoadFromFile(loadFilePath);
            Blob blob = gson.fromJson(serialized, Blob.class);
            contacts = blob.getContacts();
            lastHashes = blob.getLastHashes();
            envelopeMap = blob.getEnvelopeMap();
            speechMap = blob.getSpeechMap();
            SendRelay(zsocket, RelayType.UIResultForSaveLoad, "False", "True", date);
        } catch (Exception e) {
            System.out.println("Failed save = " + e);
            SendRelay(zsocket, RelayType.UIResultForSaveLoad, "False", "False", this.date);
            return;
        } 
    }
        
    private void SendSpeech(String sendText, String destinationKeyString) {
        
        String publicKeyString = getKeyAsString(publicKey);
        Envelope envelope = null;
        
        try {
            PublicKey recipient = CoreKeyUtil.GetPublicKeyFromText(destinationKeyString); 
            if (!lastHashes.containsKey(destinationKeyString)) {
                lastHashes.put(destinationKeyString, PasswordUtil.hash(""));
            }
            String hashOfLastMessage = lastHashes.get(destinationKeyString);
            Message message = CoreMessageUtil.makeMessage(recipient, sendText, hashOfLastMessage, publicKeyString);
            String messageText = gson.toJson(message);
            lastHashes.put(destinationKeyString, PasswordUtil.hash(messageText.toString()));
            envelope = CoreMessageUtil.makeEnvelope(recipient, privateKey, message);
            SendResponse sendResponse = CoreMessageUtil.sendEnvelope(envelope);
            if (sendResponse == null || !sendResponse.isSuccess()) {
                SendRelay(zsocket, RelayType.UIResultForSpeechSend, sendText, "False", this.date);
            } else {
                SendRelay(zsocket, RelayType.UIResultForSpeechSend, sendText, "True", this.date);
                
                String identifier = PasswordUtil.hash(envelope.toString());
                AddLocalSpeech(sendText, identifier, destinationKeyString);
            }
        } catch (Exception e) {
            System.out.println("Failed SendSpeech = " + e);
            SendRelay(zsocket, RelayType.UIResultForSpeechSend, sendText, "False", this.date);
            return;
        }

    }

    public Core() {
        gson = new Gson();
        date = new Date();

        cookieToken = null;
        zbuffer = new ArrayDeque<String>();

        lastHashes = new HashMap<String, String>();
        contacts = new HashMap<String, String>();
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
                RelayType relayType = inputRelay.getType();
                if(relayType != RelayType.CRYPTOTick) {
                    System.out.println("ReceivedFromUI: " + inputRelay);
                }

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
                case CRYPTOSave:
                    Save(inputRelay.getPrimaryData());
                    break;
                case CRYPTOLoad:
                    Load(inputRelay.getPrimaryData());
                    break;
                case CRYPTOFakeSpam:
                    {
                        String origin = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAl0b2DduYDFNyKcysDKDY3RRUB1bNs+fBdtzwDYJZhVH0eeFnpEmE3MJvJ2klcCRCeKhqziqK3Bfnx9psKull5nEOPi3dDiW54aHfw91rD05rIHa7dLyERvwevdqr8DOCM5GfkSgDgRxs4etIkrkiF+oujf97B7ivYvWgO2/6hBxOuviedk98Q01HyHNOnmaOtax/3mkM8iNV8fykLyiPTU3XNeCJmka7j9Z+mPvsbf9ZcNSrmeLrZg4jyMT+8lign2APK7QXiY5t5G4LsMhMW6Q4sOe8Dn6aNrNpcQkSLe/JMWk76LVUpDBS6k4gJc/bM5p7OJZn6sUJ5c0QQYgqaQIDAQAB";
                        String sendText = "X "+inputRelay.getSecondaryData();
                        date = new Date();
                        Instant instant = date.toInstant();
                        String identifier = PasswordUtil.hash(instant.toString());
                        System.out.println("Spam");
                        AddRemoteSpeech(sendText, identifier, origin, false, false);
                        break;
                    }
                    case CRYPTOFakeReceive:
                    {
                        String origin = inputRelay.getPrimaryData();
                        String sendText = "r "+inputRelay.getSecondaryData();
                        date = new Date();
                        Instant instant = date.toInstant();
                        String identifier = PasswordUtil.hash(instant.toString());
                        Boolean validHash = Math.random() < 0.5;
                        Boolean validSign = Math.random() < 0.5;
                        System.out.println("Rcv "+sendText);
                        AddRemoteSpeech(sendText, identifier, origin, validHash, validSign);
                        break;
                    }
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
            if (type != RelayType.UISpeechUpdate) {
                System.out.println("ToUI: " + relay.getType());
                System.out.println(".p0.: " + primary);
                System.out.println(".p1.: " + secondary);
            }
            zbuffer.add(serialized);
        } catch (Exception e) { 
            System.out.println("SendRelay exception = " + e);
        }
    }

}
