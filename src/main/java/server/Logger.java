package server;

import java.io.File;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger{
	static PrintWriter log_file;
        static int log_line_written = 0;

	/**
         * This function is used to log the activities from the server
         *
         */
        public static void log(String message){
		try{
                String timeStamp = (new SimpleDateFormat("dd_MM_yyyy@HH:mm:ss")).format(new Date());
                String timeStamp_forPrint = "["+timeStamp+"]::";
                if (log_line_written %100 == 0){
                        // the log_file needs to be recreated
                        log_file = new PrintWriter(new File("SERVER-LOG/"+timeStamp+".txt"), "UTF-8");
                        log_line_written = 0;
                }
                log_line_written ++;
                String log_message = timeStamp_forPrint+message;
                log_file.write(log_message+"\n");
		log_file.flush();
                System.out.println(log_message);
		}catch(Exception e){
                   System.out.println("LOGGER CRASHED !!!!!!!!!!!!!!   "+e.toString());
		}
        }


}
