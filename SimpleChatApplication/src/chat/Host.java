package chat;

import java.net.*;
import java.io.*;
import javax.swing.*;

public class Host extends javax.swing.JFrame {
    
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private static PrintWriter pw;
    private String guestName; // Variable to store the guest's name
    
    public Host() {
        initComponents();
        startServer();
    }
    
    private void startServer() {
        new Thread(() -> {
            try {
                serverSocket = new ServerSocket(5432);
                appendMessage("Server started. Waiting for guests...");
                clientSocket = serverSocket.accept();
                appendMessage("A guest has connected: " + clientSocket.getInetAddress());

                BufferedReader br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                pw = new PrintWriter(clientSocket.getOutputStream(), true);

                // Prompt for name
                pw.println("Welcome to the chat! Please enter your name:");

                new Thread(() -> receiveMessages(br)).start();

                inputField.addActionListener(e -> sendMessage());
                sendButton.addActionListener(e -> sendMessage());

            } catch (IOException e) {
                appendMessage("Error starting server: " + e.getMessage());
            }
        }).start();
    }
    
    private void sendMessage() {
        String message = inputField.getText();
        inputField.setText("");
        pw.println(message);
        appendMessage("You: " + message);
        if (message.equalsIgnoreCase("exit")) {
            cleanupResources();
            System.exit(0);
        }
    }
    
    private void receiveMessages(BufferedReader br) {
        try {
            String clientMessage;
            while ((clientMessage = br.readLine()) != null) {
                // Check if the message is a name message
                if (clientMessage.startsWith("Name: ")) {
                    guestName = clientMessage.substring(6); // Store the guest's name
                    appendMessage("Guest, " + guestName + ", has joined the chat: " + clientSocket.getInetAddress());
                } else {
                    appendMessage(guestName + ": " + clientMessage); // Use the guest's name
                }
            }
        } catch (IOException e) {
            appendMessage("Client disconnected.");
        }
    }

    private void appendMessage(String message) {
        SwingUtilities.invokeLater(() -> chatArea.append(message + "\n"));
    }
    
    private void cleanupResources() {
        try {
            if (clientSocket != null) clientSocket.close();
            if (serverSocket != null) serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> new Host().setVisible(true));
    }
    
    private void initComponents() {
        sendButton = new javax.swing.JButton();
        chatScrollPane = new javax.swing.JScrollPane();
        chatArea = new javax.swing.JTextArea();
        inputField = new javax.swing.JTextField();
        inputPanel = new javax.swing.JPanel();

        sendButton.setText("Send");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Host Chat");
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