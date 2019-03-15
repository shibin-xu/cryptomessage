package server;

// General Utilities

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import java.security.Security;
import java.util.Scanner;

// SSL Utilities

// RSA Utilities

// MySQL Utilities
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class ServerOps{


	public static void main(String[] args){
		Logger.log("Server starts to boot now");
		String sqlpass = getMySQLPassword();
		String keypass = getKeyStorePassword();
	        if(!trySQLConnection(sqlpass)){Logger.log("SERVER DOWN");};
		// firstly, create a server socket to accept client connection
		SSLServerSocket serverSoc = getServerSocket(keypass);
		// now forever loop
		while(serverSoc != null){
			try{
			// wait for client connection
			Logger.log("Waiting for connection .....");
			SSLSocket con = (SSLSocket)serverSoc.accept();
			// dispatch to a TaskHandler Thread
			TaskHandler task = new TaskHandler(con, sqlpass);
			// start the task and move on to the next connection
			Logger.log("New Connection has been accepted");
			task.start();
			}catch(Exception e){
                        Logger.log("Accepting failed:"+e.toString());
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
	   int port = 7729;
	   System.setProperty("javax.net.ssl.keyStore","certs/ServerKeyStore.jks");
	   System.setProperty("javax.net.ssl.keyStorePassword",keystorepassword);
	   try{
		  SSLServerSocketFactory sslServerSocketfactory = (SSLServerSocketFactory)SSLServerSocketFactory.getDefault();
		  SSLServerSocket sslServerSocket = (SSLServerSocket)sslServerSocketfactory.createServerSocket(port);
		  Logger.log("Server Socket has been Established. Ready for connections");
		  return sslServerSocket;
	   }catch(Exception e){
		Logger.log("!!!!! Socket failed to established for the following reason");
	   	Logger.log("Exception!!!  "+e.toString());
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
                        Logger.log("   MySQL Connection Cannot be established. Aborting !!!");
			return false;
                //      while(true){};
                }
                }catch(SQLException e){
                Logger.log("  MySQL Connection Cannot be established for the reason below. Aborting !!! \n "+e.toString());
                e.printStackTrace();
                return false;
                }
        }
	

	
}
