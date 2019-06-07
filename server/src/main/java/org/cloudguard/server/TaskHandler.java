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

	private Connection sqlCon = null;
	private PreparedStatement sqlPrep = null;
	private String sqlpass;

	public TaskHandler(SSLSocket con, String pass, ConcurrentMap<String, String> cookies, ConcurrentMap<String, String> publicKey2Nonce) {
		try{
			this.sqlpass = pass;
			this.connection = con;
			this.cookies = cookies;
			this.publicKey2Nonce = publicKey2Nonce;
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
				case "org.cloudguard.commons.RegisterRequest":
					register(request);
					break;
				case "org.cloudguard.commons.LoginRequest":
					login(request);
					break;
				case "org.cloudguard.commons.DecryptAESRequest":
					decrypt(request);
					break;
				case "org.cloudguard.commons.PublicKeyRequest":
					queryuser(request);
					break;
				case "org.cloudguard.commons.LoginPrepareRequest":
					handleLoginPrepareRequest(request);
					break;
				case "org.cloudguard.commons.LoginFinishRequest":
					handleLoginFinishRequest(request);
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

	public void register(Request request){
		try{
			RegisterResponse registerResponse = new RegisterResponse(true, "");

			Gson gson = new Gson();
			RegisterRequest registerRequest = gson.fromJson(request.getJson(), RegisterRequest.class);
			String email = registerRequest.getEmail();
			String password = registerRequest.getPassword();
			HashSalt newHash = PasswordUtil.hash(password, null);
			String hash = newHash.getHash();
			String token = newHash.getSalt();
			if (!makeSQLConnection()) {
				logger.fatal("Unable to establish MySQL Connection");
				return;
			}

			String query = "SELECT * FROM user_info WHERE username=?;";
			sqlPrep = sqlCon.prepareStatement(query);
			sqlPrep.setString(1, email);
			ResultSet res = sqlPrep.executeQuery();

			if (res != null && res.next()){
				registerResponse.setSuccess(false);
				registerResponse.setMessage("Registration process failed: email is used");
				Response response = new Response(registerResponse.getClass().getName(), gson.toJson(registerResponse));
				out.writeUTF(gson.toJson(response));
				return;
			}

			KeyPair keyPair = RSAEncryptUtil.generateKey();
        	PublicKey pub = keyPair.getPublic();
        	PrivateKey priv = keyPair.getPrivate();
        	String pub_str = RSAEncryptUtil.getKeyAsString(pub);
        	String priv_str = RSAEncryptUtil.getKeyAsString(priv);

        	query = "INSERT INTO user_info VALUES (?,?,?,?,?);";
        	sqlPrep = sqlCon.prepareStatement(query);
        	sqlPrep.setString(1, email);
        	sqlPrep.setString(2, hash);
        	sqlPrep.setString(3, pub_str);
        	sqlPrep.setString(4, priv_str);
        	sqlPrep.setString(5, token);
        	sqlPrep.executeUpdate();

			Response response = new Response(registerResponse.getClass().getName(), gson.toJson(registerResponse));
			out.writeUTF(gson.toJson(response));
		} catch (Exception e) {
			logger.error("register failed due to exception: \n" + e.toString());
		}
	}

	public void login(Request request){
		try{
			LoginResponse loginResponse = new LoginResponse(true, "", "");

			Gson gson = new Gson();
			LoginRequest loginRequest = gson.fromJson(request.getJson(), LoginRequest.class);
			String email = loginRequest.getEmail();
			String password = loginRequest.getPassowrd();

			if (!makeSQLConnection()) {
				logger.fatal("Unable to establish MySQL Connection");
				return;
			}
			String query = "SELECT * FROM user_info WHERE username=?;";
			sqlPrep = sqlCon.prepareStatement(query);
			sqlPrep.setString(1,email);
			ResultSet res = sqlPrep.executeQuery();
			if (res == null || res.next() == false){
				loginResponse.setSuccess(false);
				loginResponse.setMessage("User does not exist");
				Response response = new Response(loginResponse.getClass().getName(), gson.toJson(loginResponse));
				out.writeUTF(gson.toJson(response));
				return;
			}

			String real_password_hash = res.getString("pass");
			String token = res.getString("token");
			HashSalt real_hashsalt = new HashSalt(real_password_hash, token);
			HashSalt entered_hashsalt = PasswordUtil.hash(password, real_hashsalt);
			if (real_hashsalt.equals(entered_hashsalt)){
				String cookie = generateCookie();
				while (this.cookies.putIfAbsent(cookie, email) != null)
					cookie = generateCookie();
				loginResponse.setToken(cookie);
				loginResponse.setMessage(res.getString("pub"));
				Response response = new Response(loginResponse.getClass().getName(), gson.toJson(loginResponse));

				out.writeUTF(gson.toJson(response));
			}else{
				loginResponse.setSuccess(false);
				loginResponse.setMessage("Password is wrong");
				Response response = new Response(loginResponse.getClass().getName(), gson.toJson(loginResponse));
				out.writeUTF(gson.toJson(response));
			}
		} catch (Exception e){
			logger.error("login failed due to exception: \n" + e.toString());
		}
	}

	public void queryuser(Request request){
		PublicKeyResponse publicKeyResponse = new PublicKeyResponse(true, new HashMap<String, String>());
		Gson gson = new Gson();

		try{
			PublicKeyRequest publicKeyRequest = gson.fromJson(request.getJson(), PublicKeyRequest.class);
			String cookie = publicKeyRequest.getToken();
			List<String> usernames = publicKeyRequest.getUsernames();
			Map<String, String> usernameToPublickey = new HashMap<String, String>();

			for (String username : usernames) {
				if (!makeSQLConnection()) {
					logger.fatal("Unable to establish MySQL Connection");
					return;
				}
				String query = "SELECT pub FROM user_info WHERE username=?;";
				sqlPrep = sqlCon.prepareStatement(query);
				sqlPrep.setString(1,username);
				ResultSet res = sqlPrep.executeQuery();
				if (res == null || res.next() == false){
					publicKeyResponse.setSuccess(false);
					Response response =
							new Response(publicKeyResponse.getClass().getName(), gson.toJson(publicKeyResponse));
					out.writeUTF(gson.toJson(response));
					return;
				}

				usernameToPublickey.put(username, res.getString("pub"));
			}

			publicKeyResponse.setUsernameToPublickey(usernameToPublickey);
			Response response =
					new Response(publicKeyResponse.getClass().getName(), gson.toJson(publicKeyResponse));
			out.writeUTF(gson.toJson(response));
		} catch (Exception e) {
			logger.error("queryuser failed due to exception: \n" + e.toString());
		}
	}

	public void decrypt(Request request){
		DecryptAESResponse decryptAESResponse = new DecryptAESResponse(true, "");
		Gson gson = new Gson();

		try{
			DecryptAESRequest decryptAESRequest = gson.fromJson(request.getJson(), DecryptAESRequest.class);
			String cookie = decryptAESRequest.getToken();
			String encryptedAESKey = decryptAESRequest.getEncryptedAESKey();

			String email;
			if ((email = this.cookies.get(cookie)) == null) {
				decryptAESResponse.setSuccess(false);
				Response response = new Response(decryptAESResponse.getClass().getName(), gson.toJson(decryptAESResponse));
				out.writeUTF(gson.toJson(response));
				return;
			}

			if (!makeSQLConnection()) {
				logger.fatal("Unable to establish MySQL Connection");
				return;
			}
			String query = "SELECT * FROM user_info WHERE username=?;";
			sqlPrep = sqlCon.prepareStatement(query);
			sqlPrep.setString(1, email);
			ResultSet res = sqlPrep.executeQuery();
			if (res == null || res.next() == false){
				return;
			}

			String privateKeyStr = res.getString("priv");
			PrivateKey privateKey = RSAEncryptUtil.getPrivateKeyFromString(privateKeyStr);
			String decryptedAESKey = RSAEncryptUtil.decrypt(encryptedAESKey, privateKey);

			decryptAESResponse.setDecryptedAESKey(decryptedAESKey);
			Response response = new Response(decryptAESResponse.getClass().getName(), gson.toJson(decryptAESResponse));
			out.writeUTF(gson.toJson(response));
		} catch (Exception e) {
			logger.error("decrypt failed due to exception: \n" + e.toString());
		}
	}

	private boolean makeSQLConnection(){
		try{
			sqlCon = DriverManager.getConnection("jdbc:mysql://localhost:3306/USER_PARAM", "root", sqlpass);
			return sqlCon != null;
		} catch (Exception e) {
			logger.error("makeSQLConnection failed due to exception: \n" + e.toString());
			e.printStackTrace();
			return false;
		}
	}
}
