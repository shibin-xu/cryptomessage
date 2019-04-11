package org.cloudvault.ui;

import com.google.gson.Gson;
import org.cloudvault.commons.ClientUtil;
import org.cloudvault.commons.LoginResponse;
import org.cloudvault.commons.RegisterResponse;
import org.cloudvault.commons.Response;
import org.cloudvault.crypto.*;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import org.apache.commons.io.FilenameUtils;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;

public class GUIForm extends JFrame {
    private final String PIC = "resources/Shield.jpeg";
    private CG_user user;
    private String input_path;
    private String output_path;
    private boolean inputAlert;
    private boolean outputAlert;

    private JPanel rootPanel;
    private JPanel mainPanel;
    private JPanel loginPanel;
    private JLabel Username;
    private JTextField username_textArea;
    private JLabel Password;
    private JTextField password_textArea;
    private JButton loginButton;
    private JButton registerButton;
    private JPanel filePanel;
    private JPanel selectionPanel;
    private JPanel inputPanel;
    private JTextField inputTextField;
    private JButton inputButton;
    private JPanel outputPanel;
    private JTextField outputTextField;
    private JButton outputButton;
    private JPanel processPanel;
    private JButton encryptButton;
    private JButton decryptButton;
    //private JButton selectButton;

    private JPanel logoutPanel;


    public GUIForm() {

        this.setTitle("Welcome to Cloud Guard!");
        this.setSize(750, 464);
        this.setResizable(false);
        this.setContentPane(rootPanel);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.user = null;
        this.input_path = null;
        this.output_path = null;
        this.inputAlert = true;
        this.outputAlert = true;

        this.logoutPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(1, 2));
        mainPanel.add(loginPanel);
        mainPanel.add(filePanel);

        this.setLoginPanel();
        this.setFilePanel();
        this.setLogoutPanel();
    }

    /**
     * sets up login panel, adding action listener to login button and register button
     */
    private void setLoginPanel() {

        this.registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = username_textArea.getText();
                String password = password_textArea.getText();
                // TODO
                try {
                    Response response = ClientUtil.register(username, "", "", password);
                    Gson gson = new Gson();
                    RegisterResponse registerResponse =
                            (RegisterResponse) gson.fromJson(response.getJson(), Class.forName(response.getClassName()));
                    if (!registerResponse.isSuccess()) {
                        JOptionPane.showMessageDialog(GUIForm.this,
                                registerResponse.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        username_textArea.setText("");
                        password_textArea.setText("");
                    } else {
                        JOptionPane.showMessageDialog(GUIForm.this,
                                "Registration Succeeded!", "Good", JOptionPane.INFORMATION_MESSAGE);
                    }
                } catch (Exception exception) {
                    JOptionPane.showMessageDialog(GUIForm.this,
                            exception, "Error", JOptionPane.ERROR_MESSAGE);
                    username_textArea.setText("");
                    password_textArea.setText("");
                }
            }
        });

        this.loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = username_textArea.getText();
                String password = password_textArea.getText();

                try {
                    Response response = ClientUtil.login(username, password);
                    Gson gson = new Gson();
                    LoginResponse loginResponse =
                            (LoginResponse) gson.fromJson(response.getJson(), Class.forName(response.getClassName()));

                    if (!loginResponse.isSuccess()) {
                        JOptionPane.showMessageDialog(GUIForm.this,
                                loginResponse.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(GUIForm.this,
                                "Login Succeeded!", "Good", JOptionPane.INFORMATION_MESSAGE);
                        try {
                            user = new CG_user(RSAEncryptUtil.getPublicKeyFromString(loginResponse.getMessage()),
                                    username, loginResponse.getToken());
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(GUIForm.this,
                                    "Process failed! Unknown Error! Location: F-1",
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE);
                        }

                        mainPanel.removeAll();
                        mainPanel.add(logoutPanel);
                        mainPanel.add(filePanel);
                        mainPanel.revalidate();
                        mainPanel.repaint();
                    }
                    username_textArea.setText("");
                    password_textArea.setText("");
                } catch (Exception exception) {
                    JOptionPane.showMessageDialog(GUIForm.this,
                            exception, "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    /**
     * sets up file panel, adding action listener to file chosen button
     */
    private void setFilePanel() {

        selectionPanel.setLayout(new GridLayout(1, 2));
        selectionPanel.add(inputPanel);
        selectionPanel.add(outputPanel);

        inputTextField.setBackground(null);
        inputTextField.setBorder(null);
        outputTextField.setBackground(null);
        outputTextField.setBorder(null);

        inputButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!GUIForm.this.checkUserLogin()) {
                    return;
                }
                if (inputAlert) {
                    Object[] options = {"OK", "Never ask me again"};
                    int response = JOptionPane.showOptionDialog(GUIForm.this,
                            "Choose the target file that you want to encrypt/decrypt",
                            "Input File Selection", JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
                    if (response != JOptionPane.YES_OPTION) {
                        GUIForm.this.inputAlert = false;
                    }
                }

                JFileChooser chooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
                chooser.setDialogTitle("Select your input file");
                chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                if (chooser.showOpenDialog(GUIForm.this) != JFileChooser.APPROVE_OPTION) {
                    return;
                }
                String path = chooser.getSelectedFile().getAbsolutePath();
                GUIForm.this.input_path = path;
                GUIForm.this.inputTextField.setText(FilenameUtils.getName(path));
                GUIForm.this.inputButton.setText("Reselect?");
            }
        });

        outputButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!GUIForm.this.checkUserLogin()) {
                    return;
                }
                if (outputAlert) {
                    Object[] options = {"OK", "Never ask me again"};
                    int response = JOptionPane.showOptionDialog(GUIForm.this,
                            "Choose the target folder where encrypted/decrypted file will be saved",
                            "Output File Path Selection", JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
                    if (response != JOptionPane.YES_OPTION) {
                        GUIForm.this.outputAlert = false;
                    }
                }

                JFileChooser chooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
                chooser.setDialogTitle("Select your output file path");
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                if (chooser.showOpenDialog(GUIForm.this) != JFileChooser.APPROVE_OPTION) {
                    return;
                }
                String path = chooser.getSelectedFile().getAbsolutePath();
                GUIForm.this.output_path = path;
                GUIForm.this.outputTextField.setText(FilenameUtils.getName(path) + "/");
                GUIForm.this.outputButton.setText("Reselect?");
            }
        });

        encryptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!GUIForm.this.checkUserLogin() || !GUIForm.this.checkFileSelection()) {
                    return;
                }
                int response = JOptionPane.showConfirmDialog(GUIForm.this,
                        "Would you like to share this file with others?",
                        "File sharing", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

                ArrayList<String> usernames = new ArrayList<String>();
                if (response == JOptionPane.YES_OPTION) {
                    String input = JOptionPane.showInputDialog("Give me their usernames separated with comma");
                    if (input != null) {
                        String[] temp = input.split(",");
                        for (String s : temp) {
                            usernames.add(s.trim());
                        }
                    }
                }
                usernames.add(user.getUsername());
                UIMessage answer = Main.fileProcess(input_path, output_path, JOptionPane.YES_OPTION, user, usernames);
                GUIForm.this.resetFilePanel(answer);
            }
        });

        decryptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!GUIForm.this.checkUserLogin() || !GUIForm.this.checkFileSelection()) {
                    return;
                }
                UIMessage answer = Main.fileProcess(input_path, output_path, JOptionPane.NO_OPTION, user, null);
                GUIForm.this.resetFilePanel(answer);
            }
        });

        /*
        this.dndButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(GUIForm.this,
                        "File drag & drop has not been implemented yet!",
                        "Warning",
                        JOptionPane.WARNING_MESSAGE);
            }
        });
        */

    }

    private void resetFilePanel(UIMessage answer) {
        if (answer.isSuccess()) {
            JOptionPane.showMessageDialog(GUIForm.this, answer.getMessage(),
                    "Good", JOptionPane.INFORMATION_MESSAGE);
            this.input_path = null;
            this.output_path = null;
            this.inputTextField.setText("Nothing Selected");
            this.outputTextField.setText("Nothing Selected");
            this.inputButton.setText("Select here!");
            this.outputButton.setText("Select here!");
        } else {
            JOptionPane.showMessageDialog(GUIForm.this, answer.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    /**
     * sets up logout panel, adding action listener to logout button
     */
    private void setLogoutPanel() {
        JButton picButton = new JButton();
        picButton.add(new JImage(PIC));
        picButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int response = JOptionPane.showConfirmDialog(GUIForm.this,
                        "Welcome, " + user.getUsername() + "!\nDo you want to logout?",
                        "Welcome", JOptionPane.YES_NO_OPTION);
                if (response == JOptionPane.YES_OPTION) {
                    user = null;
                    mainPanel.removeAll();
                    mainPanel.add(loginPanel);
                    mainPanel.add(filePanel);
                    mainPanel.revalidate();
                    mainPanel.repaint();
                }
            }
        });
        logoutPanel.setLayout(new GridLayout(1, 1));
        logoutPanel.add(picButton);
    }

    /**
     * checks if user has logged in
     *
     * @return true if user has logged in, false otherwise
     */
    private boolean checkUserLogin() {
        if (this.user == null) {
            JOptionPane.showMessageDialog(GUIForm.this,
                    "You have not logged in yet!",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    /**
     * checks if input file / output folder have all been selected
     *
     * @return true if they are, false otherwise
     */
    private boolean checkFileSelection() {
        if (this.input_path == null) {
            JOptionPane.showMessageDialog(GUIForm.this,
                    "You have not chosen input file!",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (this.output_path == null) {
            JOptionPane.showMessageDialog(GUIForm.this,
                    "You have not chosen output folder!",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        rootPanel = new JPanel();
        rootPanel.setLayout(new GridLayoutManager(1, 2, new Insets(25, 30, 25, 30), -1, -1));
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
        rootPanel.add(mainPanel, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        filePanel = new JPanel();
        filePanel.setLayout(new GridLayoutManager(3, 1, new Insets(0, 0, 0, 0), -1, -1));
        mainPanel.add(filePanel, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        selectionPanel = new JPanel();
        selectionPanel.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        filePanel.add(selectionPanel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayoutManager(3, 1, new Insets(0, 0, 0, 0), -1, -1));
        selectionPanel.add(inputPanel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        Font label1Font = this.$$$getFont$$$(null, Font.BOLD, -1, label1.getFont());
        if (label1Font != null) label1.setFont(label1Font);
        label1.setText("Input File");
        inputPanel.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        inputButton = new JButton();
        inputButton.setText("Select here!");
        inputPanel.add(inputButton, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        inputTextField = new JTextField();
        inputTextField.setEditable(false);
        inputTextField.setEnabled(true);
        inputTextField.setHorizontalAlignment(0);
        inputTextField.setText("Nothing selected");
        inputPanel.add(inputTextField, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        outputPanel = new JPanel();
        outputPanel.setLayout(new GridLayoutManager(3, 1, new Insets(0, 0, 0, 0), -1, -1));
        selectionPanel.add(outputPanel, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        Font label2Font = this.$$$getFont$$$(null, Font.BOLD, -1, label2.getFont());
        if (label2Font != null) label2.setFont(label2Font);
        label2.setText("Output Folder");
        outputPanel.add(label2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        outputButton = new JButton();
        outputButton.setText("Select here!");
        outputPanel.add(outputButton, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        outputTextField = new JTextField();
        outputTextField.setEditable(false);
        outputTextField.setEnabled(true);
        outputTextField.setHorizontalAlignment(0);
        outputTextField.setText("Nothing selected");
        outputPanel.add(outputTextField, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        processPanel = new JPanel();
        processPanel.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        filePanel.add(processPanel, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
        processPanel.add(panel1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        encryptButton = new JButton();
        encryptButton.setText("Encrypt it");
        panel1.add(encryptButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel1.add(spacer1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        panel1.add(spacer2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
        processPanel.add(panel2, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        decryptButton = new JButton();
        decryptButton.setText("Decrypt it");
        panel2.add(decryptButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer3 = new Spacer();
        panel2.add(spacer3, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer4 = new Spacer();
        panel2.add(spacer4, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        filePanel.add(panel3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        loginPanel = new JPanel();
        loginPanel.setLayout(new GridLayoutManager(3, 1, new Insets(5, 5, 5, 5), -1, -1));
        mainPanel.add(loginPanel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        loginPanel.add(panel4, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel4.add(panel5, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel4.add(panel6, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        username_textArea = new JTextField();
        panel6.add(username_textArea, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        Username = new JLabel();
        Username.setEnabled(true);
        Font UsernameFont = this.$$$getFont$$$(null, Font.BOLD, -1, Username.getFont());
        if (UsernameFont != null) Username.setFont(UsernameFont);
        Username.setText("Username");
        panel6.add(Username, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_SOUTH, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel7 = new JPanel();
        panel7.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        loginPanel.add(panel7, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel8 = new JPanel();
        panel8.setLayout(new GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel7.add(panel8, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        Password = new JLabel();
        Password.setEnabled(true);
        Font PasswordFont = this.$$$getFont$$$(null, Font.BOLD, -1, Password.getFont());
        if (PasswordFont != null) Password.setFont(PasswordFont);
        Password.setText("Password");
        panel8.add(Password, new GridConstraints(0, 0, 1, 2, GridConstraints.ANCHOR_SOUTH, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        password_textArea = new JPasswordField();
        panel8.add(password_textArea, new GridConstraints(1, 0, 1, 2, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JPanel panel9 = new JPanel();
        panel9.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel7.add(panel9, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        loginButton = new JButton();
        loginButton.setText(" Login ");
        panel9.add(loginButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_SOUTHEAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        registerButton = new JButton();
        registerButton.setText("Register");
        panel9.add(registerButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_SOUTHWEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel10 = new JPanel();
        panel10.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        loginPanel.add(panel10, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null) {
            resultName = currentFont.getName();
        } else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
                resultName = fontName;
            } else {
                resultName = currentFont.getName();
            }
        }
        return new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return rootPanel;
    }


    public static class JImage extends JComponent {

        private BufferedImage image;

        public JImage(String path) {
            try {
                this.image = ImageIO.read(new File(path));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            double percentage = image.getWidth() / image.getHeight();
            g.drawImage(image, 0, 0, this.getWidth(), //(int)Math.floor(this.getHeight()*percentage),
                    this.getHeight(), null);
        }
    }
}
