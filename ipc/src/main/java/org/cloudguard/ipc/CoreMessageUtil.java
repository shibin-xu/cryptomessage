package org.cloudguard.ipc;

import com.google.gson.Gson;
import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.cloudguard.commons.*;
import org.cloudguard.crypto.AESEncryptUtil;
import org.cloudguard.crypto.PasswordUtil;
import org.cloudguard.crypto.RSAEncryptUtil;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.*;

import static org.cloudguard.commons.ClientUtil.getResponse;


public class CoreMessageUtil {

    public static Message makeMessage(PublicKey recipientPublicKey,
                                       String body, String hashOfLastMessage,
                                       String senderPublicKey)
            throws UnsupportedEncodingException,
            InvalidCipherTextException,
            IllegalBlockSizeException,
            InvalidKeyException,
            BadPaddingException,
            NoSuchAlgorithmException,
            NoSuchPaddingException {
        Date date = new Date();
        byte[] aesKey = AESEncryptUtil.generateKey();
        String encryptedBody = AESEncryptUtil.encrypt(body, aesKey);
        String encryptedAESKey = RSAEncryptUtil.encrypt(Base64.encodeBase64String(aesKey), recipientPublicKey);
        return new Message(encryptedBody, encryptedAESKey, hashOfLastMessage, date.getTime(), senderPublicKey);
    }

    public static SendResponse sendMessage (PublicKey recipientPublicKey, PrivateKey senderPrivateKey,
                                             Message message) throws
            NoSuchProviderException,
            NoSuchAlgorithmException,
            IOException,
            InvalidKeyException,
            SignatureException {
        Gson gson = new Gson();
        String proof = RSAEncryptUtil.sign(gson.toJson(message), senderPrivateKey);
        Envelope envelope = new Envelope(message, proof, RSAEncryptUtil.getKeyAsString(recipientPublicKey));
        List<Envelope> list = new ArrayList<>();
        list.add(envelope);

        SendRequest sendRequest = new SendRequest(list);
        Request request = new Request(sendRequest.getClass().getName(), gson.toJson(sendRequest));
        Response response = getResponse(request);
        SendResponse sendResponse = gson.fromJson(response.getJson(), SendResponse.class);

        return sendResponse;
    }

    public static GetResponse getMessage(String cookie) throws
            IOException {
        Gson gson = new Gson();
        GetRequest getRequest = new GetRequest(cookie);
        Request request = new Request(getRequest.getClass().getName(), gson.toJson(getRequest));
        Response response = getResponse(request);
        GetResponse getResponse = gson.fromJson(response.getJson(), GetResponse.class);

        return getResponse;
    }

    public static void readMessage(List<Envelope> list, PrivateKey privateKey,
                                    Map<String, List<Envelope>> publicKey2Envelope) throws
            NoSuchPaddingException,
            NoSuchAlgorithmException,
            IOException,
            BadPaddingException,
            IllegalBlockSizeException,
            InvalidKeyException,
            InvalidCipherTextException,
            InvalidKeySpecException,
            NoSuchProviderException,
            SignatureException {
        Gson gson = new Gson();
        for (Envelope envelope : list) {
            Message message = envelope.getMessage();
            System.out.println("message = ");
            System.out.println(message);
            System.out.println("public key = " + message.getSenderPublicKey());
            PublicKey senderPublicKey = RSAEncryptUtil.getPublicKeyFromString(message.getSenderPublicKey());

            boolean verified = RSAEncryptUtil.verify(gson.toJson(message), envelope.getSignature(), senderPublicKey);
            if (verified) {
                List<Envelope> envelopes;
                if (publicKey2Envelope.containsKey(message.getSenderPublicKey())) {
                    envelopes = publicKey2Envelope.get(message.getSenderPublicKey());
                } else {
                    envelopes = new ArrayList<>();
                    publicKey2Envelope.put(message.getSenderPublicKey(), envelopes);
                }

                String expectedHash = PasswordUtil.hash("");
                if (!envelopes.isEmpty()) {
                    expectedHash = PasswordUtil.hash(gson.toJson(envelopes.get(envelopes.size() - 1).getMessage()));
                }

                if (expectedHash.equals(message.getHashOfLastMessage())) {
                    String aesKey = RSAEncryptUtil.decrypt(message.getEncryptedAESKey(), privateKey);
                    String decryptedBody = AESEncryptUtil.decrypt(message.getBody(), Base64.decodeBase64(aesKey));

                    System.out.println(" Message = " + decryptedBody);
                    System.out.println();

                    envelopes.add(envelope);
                }
            }
        }
    }
}
