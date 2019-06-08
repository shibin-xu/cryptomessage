package org.cloudguard.server;

import com.google.gson.Gson;
import org.cloudguard.commons.*;
import org.cloudguard.crypto.*;

import javax.net.ssl.SSLSocket;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import static org.cloudguard.crypto.PasswordUtil.generateCookie;

public class TaskHandler extends Thread{
	private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(TaskHandler.class);

	private SSLSocket connection;
	private DataInputStream in;
	private DataOutputStream out;
	private String message;
	private ConcurrentMap<String, String> cookies;
	private ConcurrentMap<String, String> publicKey2Nonce;
	ConcurrentMap<String, List<Envelope>>  publicKey2Envelope;

	public TaskHandler(SSLSocket con, ConcurrentMap<String, String> cookies,
					   ConcurrentMap<String, String> publicKey2Nonce,
					   ConcurrentMap<String, List<Envelope>>  publicKey2Envelope) {
		try{
			this.connection = con;
			this.cookies = cookies;
			this.publicKey2Nonce = publicKey2Nonce;
			this.publicKey2Envelope = publicKey2Envelope;

			this.in = new DataInputStream(con.getInputStream());
			this.out = new DataOutputStream(con.getOutputStream());
		}catch(Exception e){
			logger.error("Initializing TaskHandler failed due to exception: \n" + e.toString());
		}
	}

	public void run(){
		try{
			this.message = in.readUTF();
			logger.info("New Message Inbound: \n" + message + "\n\n");
			Gson gson = new Gson();
			Request request = gson.fromJson(message, Request.class);
			String className = request.getClassName();

			switch (className) {
				case "org.cloudguard.commons.LoginPrepareRequest":
					handleLoginPrepareRequest(request);
					break;
				case "org.cloudguard.commons.LoginFinishRequest":
					handleLoginFinishRequest(request);
					break;
				case "org.cloudguard.commons.SendRequest":
					handleSendRequest(request);
					break;
			}

			out.close();
			in.close();
			connection.close();
		}catch(Exception e){
			logger.error("Handling request failed\n" + "Message: " + message + "\n" + "Exception: " + e.toString());
			logger.error(e.toString());
		}
	}

	public void handleLoginPrepareRequest(Request request) throws
			NoSuchProviderException,
			NoSuchAlgorithmException,
			IOException {
		String nonce = generateCookie();
		LoginPrepareResponse loginPrepareResponse = new LoginPrepareResponse(nonce);

		Gson gson = new Gson();
		LoginPrepareRequest loginPrepareRequest = gson.fromJson(request.getJson(), LoginPrepareRequest.class);
		String publicKey = loginPrepareRequest.getPublicKey();
		this.publicKey2Nonce.put(publicKey, loginPrepareRequest.getNonce() + nonce);
		Response response = new Response(loginPrepareResponse.getClass().getName(), gson.toJson(loginPrepareResponse));
		out.writeUTF(gson.toJson(response));
	}

	public void handleLoginFinishRequest(Request request) throws
			InvalidKeySpecException,
			NoSuchAlgorithmException,
			SignatureException,
			NoSuchProviderException,
			InvalidKeyException,
			IOException {
		Gson gson = new Gson();
		LoginFinishRequest loginFinishRequest = gson.fromJson(request.getJson(), LoginFinishRequest.class);
		String publicKey = loginFinishRequest.getPublicKey();
		PublicKey pub = RSAEncryptUtil.getPublicKeyFromString(publicKey);
		String signature = loginFinishRequest.getSignature();

		String nonce = this.publicKey2Nonce.get(publicKey);
		if (nonce == null)
			return;
		else
			this.publicKey2Nonce.remove(publicKey);

		boolean verified = RSAEncryptUtil.verify(nonce, signature, pub);
		String cookie = generateCookie();

		LoginFinishResponse loginFinishResponse = new LoginFinishResponse(verified, "");
		if (verified) {
			loginFinishResponse.setToken(cookie);
		}

		Response response = new Response(loginFinishResponse.getClass().getName(), gson.toJson(loginFinishResponse));
		out.writeUTF(gson.toJson(response));
	}

	private void handleSendRequest(Request request) throws
			IOException {
		Gson gson = new Gson();
		SendRequest sendRequest = gson.fromJson(request.getJson(), SendRequest.class);
		List<Envelope> list = sendRequest.getEnvelopes();

		for (Envelope envelope : list) {
			String recipientRSAPublicKey = envelope.getRecipientRSAPublicKey();
			if (!this.publicKey2Envelope.containsKey(recipientRSAPublicKey))
				this.publicKey2Envelope.put(recipientRSAPublicKey, new ArrayList<Envelope>());
			if (!this.publicKey2Envelope.get(recipientRSAPublicKey).contains(envelope))
				this.publicKey2Envelope.get(recipientRSAPublicKey).add(envelope);
		}

		SendResponse sendResponse = new SendResponse(true);
		Response response = new Response(sendResponse.getClass().getName(), gson.toJson(sendResponse));
		out.writeUTF(gson.toJson(response));

		System.out.println();
		System.out.println();
		System.out.println("publicKey2Envelope = ");
		System.out.println(publicKey2Envelope);
		System.out.println();
		System.out.println();
	}
}
