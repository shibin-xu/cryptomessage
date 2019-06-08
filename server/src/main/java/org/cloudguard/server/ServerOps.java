package org.cloudguard.server;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.cloudguard.commons.Envelope;
import org.cloudguard.crypto.CryptoUtil;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ServerOps {
	private static Logger logger = Logger.getLogger(ServerOps.class);
	private static int PORT = 7729;

	public static void main(String[] args){
		PropertyConfigurator.configure("resources/log4j.properties");

		logger.info("Server boot up.");
		String sqlpass = getMySQLPassword();
		String keypass = getKeyStorePassword();
		if (!trySQLConnection(sqlpass)) {
			logger.fatal("SERVER DOWN: unable to establish MySQL connection");
		} else {
			logger.info("MySQL connection has been Established");
		}

		SSLServerSocket serverSoc = getServerSocket(keypass);
		if (serverSoc == null) {
			logger.fatal("SERVER DOWN: unable to establish SSLServerSocket");
		} else {
			logger.info("Server Socket has been established");
		}

		CryptoUtil.init();
		ConcurrentMap<String, String> cookies = new ConcurrentHashMap<String, String>();
		ConcurrentMap<String, String> publicKey2Nonce = new ConcurrentHashMap<>();
		ConcurrentMap<String, List<Envelope>> publicKey2Envelope = new ConcurrentHashMap<>();

		CookieConsumer cookieConsumer = new CookieConsumer(cookies);
		cookieConsumer.start();

		while(serverSoc != null){
			try{
				logger.info("Waiting for connection .....");
				SSLSocket con = (SSLSocket) serverSoc.accept();
				TaskHandler task = new TaskHandler(con, cookies, publicKey2Nonce, publicKey2Envelope);
				logger.info("New Connection has been accepted " + con);
				task.start();
			}catch(Exception e){
				logger.error("Accepting connection failed due to exception: \n" + e.toString());
			}
		}
	}

        private static String getMySQLPassword(){
              Scanner scanIn = new Scanner(System.in);
	      System.out.print("Please enter your MySQL password for 'root':");
	      String sqlPassword = scanIn.nextLine();
	      return sqlPassword;
	}

	private static String getKeyStorePassword(){
              Scanner scanIn = new Scanner(System.in);
              System.out.print("Please enter your SSL Keystore password:");
              String keyPassword = scanIn.nextLine();
              return keyPassword;
        }



        /**
	 *
	 * This function generates a server socket to wait for connections
	 */
	public static SSLServerSocket getServerSocket(String keystorepassword){
		System.setProperty("javax.net.ssl.keyStore","certs/ServerKeyStore.jks");
		System.setProperty("javax.net.ssl.keyStorePassword",keystorepassword);

		try{
			SSLServerSocketFactory sslServerSocketfactory = (SSLServerSocketFactory)SSLServerSocketFactory.getDefault();
			SSLServerSocket sslServerSocket = (SSLServerSocket)sslServerSocketfactory.createServerSocket(PORT);
			return sslServerSocket;
		} catch(Exception e) {
			logger.error("SSLServerSocket can't be established due to exception: \n" + e.toString());
			return null;
	   }
	}

	private static boolean trySQLConnection(String sqlpassword){
		Connection sqlCon;
		try{
			sqlCon = DriverManager.getConnection("jdbc:mysql://localhost:3306/USER_PARAM", "root", sqlpassword);
			if (sqlCon != null){
				return true;
			} else {
				return false;
			}
		} catch(SQLException e) {
			logger.error("MySQL Connection can't be established due to exception: \n" + e.toString());
			return false;
		}
	}
}
