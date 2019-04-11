package org.cloudvault.commons;

import com.google.gson.Gson;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import static org.cloudvault.commons.UsernameSanitizer.validateUsername;


public class ClientUtil {

    static int serverPort = 7729;
    static String serverName = null;
    static boolean initialized = false;


    private static void init(){
	try{
        if (serverName == null){
           Scanner scanIn = new Scanner(new File("NET.config"));
	   serverName = scanIn.nextLine();
	}
	}catch(Exception e){
	}
    }

    public static Response register(String email, String firstName, String lastName, String password) throws IOException {
        if (!validateUsername(email)) {
            return null;
        }

        Gson gson = new Gson();
        RegisterRequest registerRequest = new RegisterRequest(email, firstName, lastName, password);

        Request request = new Request(registerRequest.getClass().getName(), gson.toJson(registerRequest));
        return askAndReceive(establishConnection(), request);
    }

    public static Response login(String email, String password) throws IOException {
        if (!validateUsername(email)) {
            return null;
        }

        Gson gson = new Gson();
        LoginRequest loginRequest = new LoginRequest(email, password);

        Request request = new Request(loginRequest.getClass().getName(), gson.toJson(loginRequest));
        return askAndReceive(establishConnection(), request);
    }

    public static Response queryuser(String token, List<String> usernames) throws IOException {
        Gson gson = new Gson();
        PublicKeyRequest publicKeyRequest = new PublicKeyRequest(token, usernames);

        Request request = new Request(publicKeyRequest.getClass().getName(), gson.toJson(publicKeyRequest));
        return askAndReceive(establishConnection(), request);
    }

    public static Response decryptSymmetric(String token, String encryptedAESKey) throws IOException {
        Gson gson = new Gson();
        DecryptAESRequest decryptAESRequest = new DecryptAESRequest(token, encryptedAESKey);

        Request request = new Request(decryptAESRequest.getClass().getName(), gson.toJson(decryptAESRequest));
        return askAndReceive(establishConnection(), request);
    }

    private static Response askAndReceive(SSLSocket con, Request request)
        throws IOException {
        Gson gson = new Gson();

        DataInputStream in = new DataInputStream(con.getInputStream());
        DataOutputStream out = new DataOutputStream(con.getOutputStream());

        out.writeUTF(gson.toJson(request));
        return gson.fromJson(in.readUTF(), Response.class);
    }

    private static SSLSocket establishConnection() {
	init();
        if (!initialized) {
            //specifing the trustStore file which contains the certificate & public of the server
            System.setProperty("javax.net.ssl.trustStore", "TrustWorthyStore.jts");
            //specifing the password of the trustStore file
	    String pass = null;
	    try{
	    	Scanner scanIn = new Scanner(new File("certs/tmp.tmp"));
	        pass = scanIn.nextLine();
	    }catch(Exception ex){
	    }
            System.setProperty("javax.net.ssl.trustStorePassword", pass);
        }
        initialized = true;  // Initialization was complete.
        try {
            SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            SSLSocket sslSocket = (SSLSocket) sslsocketfactory.createSocket(serverName, serverPort);
            return sslSocket;
        } catch (Exception ex) {
            System.err.println("SSL Connection error Happened : " + ex.toString());
            return null;
        }
    }

}
