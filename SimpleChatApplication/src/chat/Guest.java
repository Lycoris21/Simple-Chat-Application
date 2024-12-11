package chat;

import java.net.*;
import java.io.*;
import javax.swing.*;

public class Guest extends javax.swing.JFrame {
    
    private Socket clientSocket;
    private static PrintWriter pw;
    
    public Guest() {
        initComponents();
        connectToServer();
    }
    
    private void connectToServer(){
        new Thread(() -> {
            try {
                clientSocket = new Socket("127.0.0.1", 5432);
                BufferedReader br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                pw = new PrintWriter(clientSocket.getOutputStream(), true);

                appendMessage(br.readLine());

                String name = JOptionPane.showInputDialog(this, "Enter your name:");
                if (name != null && !name.trim().isEmpty()) {
                    pw.println("Name: " + name);
                    appendMessage("You: " + name);
                }

                new Thread(() -> receiveMessages(br)).start();

                inputField.addActionListener(e -> sendMessage());
                sendButton.addActionListener(e -> sendMessage());

                inputField.addFocusListener(new java.awt.event.FocusAdapter() {
                    public void focusGained(java.awt.event.FocusEvent evt) {
                        if (inputField.getText().equals("Type message here...")) {
                            inputField.setText("");
                        }
                    }
                });

            } catch (IOException e) {
                appendMessage("Error connecting to server: " + e.getMessage());
            }
        }).start();
    }
    
    private void sendMessage() {
        String message = inputField.getText();
        inputField.setText("");
        pw.println(message);
        appendMessage("You: " + message);
        if (message.equalsIgnoreCase("exit")) {
            try {
                clientSocket.close();
                System.exit(0);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    private void receiveMessages(BufferedReader br) {
        try {
            String serverMessage;
            while ((serverMessage = br.readLine()) != null) {
                appendMessage("Host: " + serverMessage);
            }
        } catch (IOException e) {
            appendMessage("Server disconnected.");
        }
    }
    
    private void appendMessage(String message) {
        SwingUtilities.invokeLater(() -> chatArea.append(message + "\n"));
    }
    
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> new Guest().setVisible(true));
    }
    
    private void initComponents() {
        sendButton = new javax.swing.JButton();
        chatScrollPane = new javax.swing.JScrollPane();
        chatArea = new javax.swing.JTextArea();
        inputField = new javax.swing.JTextField();
        inputPanel = new javax.swing.JPanel();

        sendButton.setText("Send");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Guest Chat");
        setMinimumSize(new java.awt.Dimension(500, 500));

        chatArea.setEditable(false);
        chatArea.setColumns(20);
        chatArea.setRows(5);
        chatScrollPane.setViewportView(chatArea);

        // Set the layout for the input panel
        inputPanel.setLayout(new java.awt.BorderLayout());
        inputPanel.add(inputField, java.awt.BorderLayout.CENTER); // Input field takes maximum width
        inputPanel.add(sendButton, java.awt.BorderLayout.EAST); // Send button on the right

        getContentPane().add(chatScrollPane, java.awt.BorderLayout.CENTER);
        getContentPane().add(inputPanel, java.awt.BorderLayout.PAGE_END);

        pack();
    }
    
    private javax.swing.JTextArea chatArea;
    private javax.swing.JScrollPane chatScrollPane;
    private javax.swing.JTextField inputField;
    private javax.swing.JButton sendButton;
    private javax.swing.JPanel inputPanel;
}