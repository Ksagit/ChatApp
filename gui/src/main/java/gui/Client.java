package gui;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.config.Configurator;

public class Client {
    private String username;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private static final Logger Logger = LogManager.getLogger(Client.class);
    private final String host;
    private final int port;

    public Client(String host, int port) {
        this.host = host;
        this.port = port;
        connectToServer(host, port);
    }

    private void connectToServer(String host, int port) {
        try {
            socket = new Socket(host, port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            String log4jConfigPath = getClass().getClassLoader().getResource("client_log4j_config.xml").getPath();
            Configurator.initialize(null, log4jConfigPath);
            Logger.info("Connected to server.");
        } catch (IOException e) {
            Logger.error("Error connecting to server: " + e.getMessage());
            retryConnection(host, port);
        }
    }

    private void retryConnection(String host, int port) {
        try {
            Thread.sleep(5000);
            Logger.info("Retrying connection to server...");
            connectToServer(host, port);
        } catch (InterruptedException e) {
            Logger.error("Connection retry interrupted: " + e.getMessage());
        }
    }

    public void joinServer(String username) {
        this.setUsername(username);
        out.println(String.format("%s %s", "addUser", username));
        Logger.info("Joined server with username: " + username);
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void refreshUsers(JTextArea userList) {
        try {
            userList.setText("");
            Logger.info("Refreshing user list");
            out.println(String.format("%s", "getUsers"));
            ObjectInputStream objectIn = new ObjectInputStream(socket.getInputStream());
            String[] users = (String[]) objectIn.readObject();
            for (String user : users) {
                userList.append(user + "\n");
            }
        } catch (Exception e) {
            Logger.error("Error refreshing user list", e);
            retryConnection(this.host, this.port);
        }
    }

    public void sendMessage(String to, String message, JTextArea chatHistory) throws IOException {
        try {
            out.println(String.format("%s %s %s %s", "addMessage", this.username, to, message));
            if (in.readLine().equals("ok")) {
                chatHistory.append(String.format("%s: %s\n", this.username, message));
                Logger.info("Sent message from " + this.username + " to " + to + ": " + message);
            }
        } catch (Exception e) {
            Logger.error("Error sending message", e);
            retryConnection(this.host, this.port);
        }
    }

    public void refreshMessages(JTextArea chatHistory, String to) throws IOException, ClassNotFoundException {
        chatHistory.setText("");
        out.println(String.format("%s %s %s", "getMessages", this.username, to));
        Logger.info("Receiving messages");
        ObjectInputStream objectIn = new ObjectInputStream(socket.getInputStream());
        String[] messages = (String[]) objectIn.readObject();
        for (String message : messages) {
            chatHistory.append(message + '\n');
        }
    }

    public void searchMessage(String to, String message, JTextArea searchHistory) throws IOException {
        try {
            //leave this for later
            Logger.info("Searching for messages from " + this.username + " to " + to + ": " + message);
            out.println(String.format("%s %s %s %s", "getMessages", this.username, to, message));
            ObjectInputStream objectIn = new ObjectInputStream(socket.getInputStream());
            String[] messages = (String[]) objectIn.readObject();
            searchHistory.append(String.format("%s %s\n", this.username, String.join(", ", messages)));
            String response = in.readLine();
            if (response.equals("ok")) {
                searchHistory.append(String.format("%s %s\n", this.username, String.join(", ", messages)));
            }
        } catch (Exception e) {
            Logger.error("Error searching for messages", e);
            retryConnection(this.host, this.port);
        }
    }
}
