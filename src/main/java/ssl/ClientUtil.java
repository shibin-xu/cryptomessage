package ssl;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.util.Scanner;


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

    public static ServerResponse register(String username, String password) {

        if (username.contains("{") || username.contains("}") || password.contains("{") || password.contains("}")) {
            return new ServerResponse(false, "username or password contains '{' or '}'");
        }
        String message = "R";
        message = message + "{username}{" + username + "}{password}{" + password + "}";
        SSLSocket con = establishConnection();
        String response = askAndReceive(con, message);
        if (response.charAt(0) == '1'){
            // success response
            ServerResponse ret = new ServerResponse(true, response.substring(1));
            return ret;
        }else {
            // error occurred
            ServerResponse ret = new ServerResponse(false, response.substring(1));
            return ret;
        }
    }

    public static ServerResponse login(String username, String password) {

        if (username.contains("{") || username.contains("}") || password.contains("{") || password.contains("}")) {
            return new ServerResponse(false, "username or password contains '{' or '}'");
        }
        String message = "L";
        message = message + "{username}{" + username + "}{password}{" + password + "}";
        SSLSocket con = establishConnection();
        String response = askAndReceive(con, message);
        if (response.charAt(0) == '1'){
            // success response
            ServerResponse ret = new ServerResponse(true, response.substring(1));
            return ret;
        }else {
            // error occurred
            ServerResponse ret = new ServerResponse(false, response.substring(1));
            return ret;
        }
    }

    public static ServerResponse queryuser(String username) {

        if (username.contains("{") || username.contains("}") ) {
            return new ServerResponse(false, "username contains '{' or '}'");
        }
        String message = "Q";
        message = message + "{username}{" + username + "}";
        SSLSocket con = establishConnection();
        String response = askAndReceive(con, message);
        if (response.charAt(0) == '1'){
            // success response
            ServerResponse ret = new ServerResponse(true, response.substring(1));
            return ret;
        }else {
            // error occurred
            ServerResponse ret = new ServerResponse(false, response.substring(1));
            return ret;
        }
    }


    public static ServerResponse decryptSymmetric(String username, String password,String symmetric) {

        if (username.contains("{") || username.contains("}") || password.contains("{") || password.contains("}") || symmetric.contains("{") || symmetric.contains("}")) {
            return new ServerResponse(false, "username or password or symmetric key contains '{' or '}'");
        }
        String message = "D";
        message = message + "{username}{" + username + "}{password}{" + password + "}{pub(sym)}{"+symmetric+"}";
        SSLSocket con = establishConnection();
        String response = askAndReceive(con, message);
        if (response.charAt(0) == '1'){
            // success response
            ServerResponse ret = new ServerResponse(true, response.substring(1));
            return ret;
        }else {
            // error occurred
            ServerResponse ret = new ServerResponse(false, response.substring(1));
            return ret;
        }
    }


    private static String askAndReceive(SSLSocket con, String message) {
        String response = "0Exception raised";
        try {
            DataOutputStream outputStream = new DataOutputStream(con.getOutputStream());
            DataInputStream inputStream = new DataInputStream(con.getInputStream());
            outputStream.writeUTF(message);
            response = inputStream.readUTF();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            return response;
        }
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
