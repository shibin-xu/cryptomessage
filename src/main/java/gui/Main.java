package gui;

import crypto.AESEncryptUtil;
import crypto.CryptoUtil;
import crypto.FileEncryptUtil;
import crypto.RSAEncryptUtil;
import org.apache.commons.io.FilenameUtils;
import ssl.ServerResponse;

import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

/**
 * Main class of User Interface
 */
public class Main {
    public static void main(String[] args) throws ClassNotFoundException,
            UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        CryptoUtil.init();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                GUIForm myGUIForm = new GUIForm();
                myGUIForm.setVisible(true);
            }
        });
    }

    /**
     * encrypts or decrypts the file saved at input address, and saves the outcome at output address
     * @param input input file path
     * @param output output folder path
     * @param response YES for encryption, NO for decryption
     * @param user user information
     * @return ServerResponse includes:
     *      a boolean indicate whether process succeeded or failed
     *      a string message
     */
    public static ServerResponse fileProcess(String input, String output, int response, CG_user user, ArrayList<String> usernames){

        RandomAccessFile input_raf;
        try {
            input_raf = new RandomAccessFile(input, "r");
        } catch (FileNotFoundException e){
            e.printStackTrace();
            return new ServerResponse(false, "Process failed! Not able to read the input file!");
        }

        byte[] key = null;
        String fileName = FilenameUtils.getName(input);
        String extension;
        if(response == JOptionPane.YES_OPTION){
            key = AESEncryptUtil.generateKey();
            extension = FilenameUtils.getExtension(fileName);
            String prefix = extension.equals("") ? "" : ".";
            extension = prefix + extension + ".encrypted";
        }else{
            extension = "";
        }
        output = output + "/" +
                FilenameUtils.removeExtension(fileName) + extension;

        File output_file = new File(output);
        boolean newFile;
        try {
            newFile = output_file.createNewFile();
        } catch(IOException e){
            e.printStackTrace();
            return new ServerResponse(false, "Process failed! Unknown Error! Location: MFP-1");
        }
        if(!newFile) {
            return new ServerResponse(false, "Process failed! Not able to create a new output file!");
        }

        RandomAccessFile output_raf;
        try {
            output_raf = new RandomAccessFile(output, "rw");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return new ServerResponse(false, "Process failed! Not able to read or write the output file!");
        }

        if(response == JOptionPane.YES_OPTION){
            try {
                FileEncryptUtil.encrypt(input_raf, output_raf, key, usernames);
            } catch(Exception e){
                e.printStackTrace();
                return new ServerResponse(false, "Process failed! Not able to encrypt this file!");
            }
            return new ServerResponse(true, "Encryption succeeded!");
        }else{
            try {
                FileEncryptUtil.decrypt(input_raf, output_raf, user.getUsername(), user.getPassword());
            } catch (Exception e){
                e.printStackTrace();
                return new ServerResponse(false, "Process failed! Not able to decrypt this file!");
            }
            return new ServerResponse(true, "Decryption succeeded!");
        }
    }
}
