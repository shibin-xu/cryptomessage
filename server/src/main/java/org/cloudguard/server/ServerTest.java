package org.cloudguard.server;

import com.google.gson.Gson;
import org.cloudguard.commons.LoginPrepareRequest;
import org.cloudguard.commons.LoginPrepareResponse;
import org.cloudguard.commons.Request;
import org.cloudguard.commons.Response;
import org.cloudguard.crypto.CryptoUtil;
import org.cloudguard.crypto.RSAEncryptUtil;

import java.io.IOException;
import java.security.*;
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
            NoSuchProviderException {
        System.out.println("ServerTest");

//        String email = prompt("Please enter your email: ");
//        String password = prompt("Please enter your password: ");
//
//        Response response = ClientUtil.login(email, password);
//        System.out.println(response);

        gson = new Gson();
        CryptoUtil.init();
        keyPair = RSAEncryptUtil.generateKey();
        pub = keyPair.getPublic();
        pri = keyPair.getPrivate();

        String nonce = generateCookie();
        String publicKey = getKeyAsString(pub);

        LoginPrepareRequest loginPrepareRequest = new LoginPrepareRequest(publicKey, nonce);
        Request request = new Request(loginPrepareRequest.getClass().getName(), gson.toJson(loginPrepareRequest));
        Response response = getResponse(request);
        LoginPrepareResponse loginPrepareResponse = gson.fromJson(response.getJson(), LoginPrepareResponse.class);

        System.out.println("loginPrepareResponse = " + loginPrepareResponse);
    }

    private static String prompt(String message) {
        Scanner scanIn = new Scanner(System.in);
        System.out.print(message);
        return scanIn.nextLine();
    }
}
