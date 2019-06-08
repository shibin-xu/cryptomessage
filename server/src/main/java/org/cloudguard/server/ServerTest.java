package org.cloudguard.server;

import com.google.gson.Gson;
import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.cloudguard.commons.*;
import org.cloudguard.crypto.AESEncryptUtil;
import org.cloudguard.crypto.CryptoUtil;
import org.cloudguard.crypto.PasswordUtil;
import org.cloudguard.crypto.RSAEncryptUtil;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import static org.cloudguard.commons.ClientUtil.getResponse;
import static org.cloudguard.crypto.PasswordUtil.generateCookie;
import static org.cloudguard.crypto.RSAEncryptUtil.getKeyAsString;

public class ServerTest {
    protected static Gson gson;
    protected static KeyPair keyPair;
    protected static PublicKey pub;
    protected static PrivateKey pri;

    public static void main(String[] args) throws
            IOException,
            NoSuchAlgorithmException,
            NoSuchProviderException,
            SignatureException,
            InvalidKeyException,
            InvalidCipherTextException,
            BadPaddingException,
            NoSuchPaddingException,
            IllegalBlockSizeException {
        System.out.println("ServerTest");
        gson = new Gson();
        CryptoUtil.init();
        keyPair = RSAEncryptUtil.generateKey();
        pub = keyPair.getPublic();
        pri = keyPair.getPrivate();

        String n1 = generateCookie();
        String publicKey = getKeyAsString(pub);

        LoginPrepareRequest loginPrepareRequest = new LoginPrepareRequest(publicKey, n1);
        Request request = new Request(loginPrepareRequest.getClass().getName(), gson.toJson(loginPrepareRequest));
        Response response = getResponse(request);
        LoginPrepareResponse loginPrepareResponse = gson.fromJson(response.getJson(), LoginPrepareResponse.class);
        System.out.println("loginPrepareResponse = " + loginPrepareResponse);
        System.out.println();

        String n2 = loginPrepareResponse.getNonce();
        String n1n2 = n1 + n2;
        String signature = RSAEncryptUtil.sign(n1n2, pri);
        LoginFinishRequest loginFinishRequest = new LoginFinishRequest(publicKey, signature);
        request = new Request(loginFinishRequest.getClass().getName(), gson.toJson(loginFinishRequest));
        response = getResponse(request);
        LoginFinishResponse loginFinishResponse = gson.fromJson(response.getJson(), LoginFinishResponse.class);
        System.out.println("loginFinishResponse = " + loginFinishResponse);
        System.out.println();

        // send a message to myself
        SendResponse sendResponse = sendMessage(pub, pri, "This is a test", PasswordUtil.hash(""));
        System.out.println("sendResponse = " + sendResponse);
        System.out.println();

        // receive the message snet
        GetResponse getResponse = getMessage(loginFinishResponse.getToken());
        System.out.println("getResponse = " + getResponse);
        System.out.println();
    }

    private static String prompt(String message) {
        Scanner scanIn = new Scanner(System.in);
        System.out.print(message);
        return scanIn.nextLine();
    }

    private static SendResponse sendMessage (PublicKey recipientPublicKey, PrivateKey senderPrivateKey,
                                             String body, String hashOfLastMessage ) throws
            NoSuchProviderException,
            NoSuchAlgorithmException,
            IOException,
            InvalidCipherTextException,
            IllegalBlockSizeException,
            InvalidKeyException,
            BadPaddingException,
            NoSuchPaddingException, SignatureException {
        Gson gson = new Gson();
        Date date = new Date();
        byte[] aesKey = AESEncryptUtil.generateKey();
        String encryptedBody = AESEncryptUtil.encrypt(body, aesKey);
        String encryptedAESKey = RSAEncryptUtil.encrypt(Base64.encodeBase64String(aesKey), recipientPublicKey);

        Message message = new Message(encryptedBody, encryptedAESKey, hashOfLastMessage, date.getTime());
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

    private static GetResponse getMessage(String cookie) throws
            IOException {
        Gson gson = new Gson();
        GetRequest getRequest = new GetRequest(cookie);
        Request request = new Request(getRequest.getClass().getName(), gson.toJson(getRequest));
        Response response = getResponse(request);
        GetResponse getResponse = gson.fromJson(response.getJson(), GetResponse.class);

        return getResponse;
    }
}
