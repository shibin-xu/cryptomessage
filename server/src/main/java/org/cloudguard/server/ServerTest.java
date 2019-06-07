package org.cloudguard.server;

import com.google.gson.Gson;
import org.cloudguard.commons.*;
import org.cloudguard.crypto.CryptoUtil;
import org.cloudguard.crypto.RSAEncryptUtil;

import java.io.IOException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
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
            InvalidKeyException, InvalidKeySpecException {
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
    }

    private static String prompt(String message) {
        Scanner scanIn = new Scanner(System.in);
        System.out.print(message);
        return scanIn.nextLine();
    }
}
