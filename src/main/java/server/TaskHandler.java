package server;

// General Utilities
import java.io.*;
import java.util.*;
import java.text.*;
import java.lang.*;

// SSL Utilities
import java.io.DataInputStream;
import java.io.DataOutputStream;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import java.security.Security;

// RSA & Password Utilities
import crypto.*;
import java.security.*;

// MySQL Utilities
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TaskHandler extends Thread{

	private SSLSocket connection;
	private DataInputStream in;
	private DataOutputStream out;
	private String message;

	private Connection sqlCon = null;
	private PreparedStatement sqlPrep = null;
        private String sqlpass; 

	public TaskHandler(SSLSocket con, String pass){
		// set up the basic configuration
		try{
			sqlpass = pass;
			connection = con;
			in = new DataInputStream(con.getInputStream());
			out = new DataOutputStream(con.getOutputStream());
		}catch(Exception e){
			Logger.log(e.toString());
		}
	}

	public void run(){
		try{
			// receive a message from the client
			message = in.readUTF();
			CryptoUtil.init();
			// determine the purpose of the client
			Logger.log("New Message Inbound");
			switch(message.charAt(0)){
				// testing reply
				case 'T': testReply(message); break;
				// Register
				case 'R': register(message); break;
				// Login
				case 'L': login(message); break;
				// decrypt
				case 'D': decrypt(message); break;
				// query for user
				case 'Q': queryuser(message); break;
			}
			// mission completed. Get out
        	        out.close();
			in.close();
			connection.close();
		}catch(Exception e){
			Logger.log(e.toString());
		}
	}


	public void register(String message){
		try{
		// parse out message
		HashMap<String, String> info = parseMessage(message);
		// get the username and password
		String username = info.get("username");
		String password = info.get("password");
		Logger.log("New user attempt to register:");
		Logger.log("    Username: "+username);
		// configure its password hash
                HashSalt newHash = PasswordUtil.hash(password, null);
		String hash = newHash.getHash();
		String token = newHash.getSalt();
		Logger.log("("+username+")"+"Checking for username availability.....");
		// connect to MySQL to check the username uniqueness
                makeSQLConnection();
		// if it is good, select rows with that user name, see if it is null
		String query = "SELECT * FROM user_info WHERE username=?;";
                sqlPrep = sqlCon.prepareStatement(query);
		sqlPrep.setString(1,username);
		ResultSet res = sqlPrep.executeQuery();
		if (res != null && res.next()){
		   // there is something there already
		   // registration failed
                   Logger.log("("+username+")"+"Duplicate was detected, abort registration!");
		   out.writeUTF("0User with the same username exists, please choose another one");
		   return;
		}
		// we can register this fellow
		Logger.log("("+username+")"+"Username is valid, starts registration");
	        // so we need to generate RSA key pairs for it.
		CryptoUtil.init();
		KeyPair keyPair = RSAEncryptUtil.generateKey();
        	PublicKey pub = keyPair.getPublic();
        	PrivateKey priv = keyPair.getPrivate();
		String pub_str = RSAEncryptUtil.getKeyAsString(pub);
		String priv_str = RSAEncryptUtil.getKeyAsString(priv);
		Logger.log("("+username+")"+"Key pair generated.");
		// now we add it to the database
		query = "INSERT INTO user_info VALUES (?,?,?,?,?);";
                sqlPrep = sqlCon.prepareStatement(query);
                sqlPrep.setString(1, username);
                sqlPrep.setString(2, hash);
                sqlPrep.setString(3, pub_str);
                sqlPrep.setString(4, priv_str);
                sqlPrep.setString(5, token);
                sqlPrep.executeUpdate();
                Logger.log("("+username+")"+"Registration was successful!!!");
		out.writeUTF("1Registration was successful!!!");
		}catch(Exception e){
		Logger.log("Registration process failed: "+e.toString());

		}
	}

	public void login(String message){
		try{
                // parse out message
                HashMap<String, String> info = parseMessage(message);
                // get the username and password
                String username = info.get("username");
                String password = info.get("password");
                Logger.log("One user attempts to login:");
                Logger.log("    Username: "+username);
		Logger.log("("+username+")"+"Confirming Identity....");
	        // connect to MySQL to check the username uniqueness
                makeSQLConnection();
	  	// check if the password was correct
		String query = "SELECT * FROM user_info WHERE username=?;";
                sqlPrep = sqlCon.prepareStatement(query);
		sqlPrep.setString(1,username);
                ResultSet res = sqlPrep.executeQuery();
		if (res == null || res.next() == false){
			// user DNE
			Logger.log("("+username+")"+"User does not exist!! Aborting");
			out.writeUTF("0User does not exist!!!");
			return;
		}
                // user exists, check the password
		/*
		 *  !!!!!!!! Now using Hash !!!!!!!!!!
		 *
		 */
		String real_password_hash = res.getString("pass");
		String token = res.getString("token");
		HashSalt real_hashsalt = new HashSalt(real_password_hash, token);
		HashSalt entered_hashsalt = PasswordUtil.hash(password, real_hashsalt);
		if (real_hashsalt.equals(entered_hashsalt)){
			// identity confirmed send back the pub key
			Logger.log("("+username+")"+"Identity Confirmed. Sending Back Public Key");
		        out.writeUTF("1"+res.getString("pub"));
		        return;	
		}else{
			// incorrect password
			Logger.log("("+username+")"+"Identity Confirmation failed: Password is wrong");
			out.writeUTF("0Password is wrong");
			return;
		}
		}catch(Exception e){
                Logger.log("Login process failed: "+e.toString());
                }
	}


	public void queryuser(String message){
        	try{
                // parse out message
                HashMap<String, String> info = parseMessage(message);
                // get the username and password
                String username = info.get("username");
                Logger.log("Query request inbound:");
                Logger.log("   Requested Username: "+username);
                // connect to MySQL to check the username uniqueness
                makeSQLConnection();
		// check if the user exits
                String query = "SELECT pub FROM user_info WHERE username=?;";
                sqlPrep = sqlCon.prepareStatement(query);
                sqlPrep.setString(1,username);
                ResultSet res = sqlPrep.executeQuery();
                if (res == null || res.next() == false){
                        // user DNE
                        Logger.log("("+username+")"+"User does not exist!! Aborting");
                        out.writeUTF("0User does not exist!!!");
                        return;
                }
                 Logger.log("Found Sending Back Public Key");
                 out.writeUTF("1"+res.getString("pub"));
                 return;

		}catch(Exception e){
                Logger.log("Login process failed: "+e.toString());
                }


	}

	public void decrypt(String message){
		
		// (-_-|||)  : THIS IS OBVIOUSLY CODE DUPLICATION.
		
		
		// ( ^ o ^)  : BUT THE TRUE NATURE OF HUMAN IS A REPEATER !!!!
		
		
		try{
                // parse out message
                HashMap<String, String> info = parseMessage(message);
                // get the username and password
                String username = info.get("username");
                String password = info.get("password");
                Logger.log("One user attempts to decrypt the symmetric key:");
                Logger.log("    Username: "+username);
                Logger.log("("+username+")"+"Confirming Identity....");
                // connect to MySQL to check the username uniqueness
                makeSQLConnection();
                // check if the password was correct
                String query = "SELECT * FROM user_info WHERE username=?;";
                sqlPrep = sqlCon.prepareStatement(query);
		sqlPrep.setString(1, username);
                ResultSet res = sqlPrep.executeQuery();
                if (res == null || res.next() == false){
                        // user DNE
                        Logger.log("("+username+")"+"User does not exist!! Aborting");
                        out.writeUTF("0User does not exist!!!");
                        return;
                }
                // user exists, check the password
                /*
                 *  !!!!! Using Hash Now !!!!!!!!!!
                 *
                 */
                String real_password_hash = res.getString("pass");
                String token = res.getString("token");
                HashSalt real_hashsalt = new HashSalt(real_password_hash, token);
                HashSalt entered_hashsalt = PasswordUtil.hash(password, real_hashsalt);
                if (real_hashsalt.equals(entered_hashsalt)){
                        // identity confirmed. Starts decrypting
                        Logger.log("("+username+")"+"Identity Confirmed. Starts decrypting..");
			String en_sym = info.get("pub(sym)");
			Logger.log("("+username+")"+"Encrypted Symmetric Key received...");
			String priv_str = res.getString("priv");
			// convert the priv str to priv key
			PrivateKey real_priv = RSAEncryptUtil.getPrivateKeyFromString(priv_str);
			Logger.log("("+username+")"+"Private key retrived from database");
   		        String decrypted = RSAEncryptUtil.decrypt(en_sym, real_priv);
			Logger.log("("+username+")"+"Decrypted. Sending it back.....");
                        out.writeUTF("1"+decrypted);  
                        return;
                }else{
                        // incorrect password
                        Logger.log("("+username+")"+"Identity Confirmation failed: Password is wrong");
                        out.writeUTF("0Password is wrong");
                        return;
		}
                }catch(Exception e){
                Logger.log("Decryption process failed: "+e.toString());
                }

	}

	public void testReply(String message){
		try{
			Logger.log("Identified as a test, simply copy and reply");
			out.writeUTF(message);
		}catch(Exception e){
			Logger.log(e.toString());
		}
	}

	private void makeSQLConnection(){

		try{
		sqlCon = DriverManager.getConnection("jdbc:mysql://localhost:3306/USER_PARAM", "root", sqlpass);
		if (sqlCon != null){
			Logger.log("   MySQL Connection Established.");
		} else {
			Logger.log("   MySQL Connection Failed !!!");
		//	while(true){};
		}
		}catch(SQLException e){
		Logger.log("   SQL Exception: "+e.toString());
		e.printStackTrace();
		return;
		}
	}

	// for parsing the message comming in
	private HashMap<String, String> parseMessage(String message){
		// The message is in this format: [Letter for type]{name}{value}{}{}{}
		// So no other values can have { or } !!!!!!
		HashMap<String, String> parsed = new HashMap<>();
		int mode = -1; //0 for null state, 1 for key parsing, -1 for value parsing
		StringBuilder keyBuilder = new StringBuilder();
		StringBuilder valueBuilder = new StringBuilder();
		for (int i=1; i<message.length(); i++){
			if (message.charAt(i) == '{'){
				// new stuff, ignore the {
				mode = -1*mode;
				continue;
			}
			if (message.charAt(i) == '}'){
				// end of parsing
				if (mode == -1){
					parsed.put(keyBuilder.toString(), valueBuilder.toString());
					keyBuilder = new StringBuilder();
					valueBuilder = new StringBuilder();
				}
				continue;
			}
			// just a normal character, determine where it goes
			if (mode == 1){
		        	keyBuilder.append(message.charAt(i));
			}else{
				valueBuilder.append(message.charAt(i));
			}
		}
		return parsed;
	}



}
