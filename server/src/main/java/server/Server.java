package server;

import java.io.*;
import java.net.*;
import java.util.*;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.config.Configurator;

public class Server {
    private static final Logger Logger = LogManager.getLogger("Server");
    private final Map<Set<String>, List<String>> messageDatabase = new HashMap<>();
    private final Map<String, Socket> onlineUsers = new HashMap<>();

    public void run(int port) {
        String log4jConfigPath = "resources/server_log4j_config.xml";
        Configurator.initialize(null, log4jConfigPath);
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            Logger.info("Server started on port: " + port);
            while (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                Thread clientThread = new Thread(() -> {
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                        while (true) {
                            String command = reader.readLine();
                            this.handler(command, socket);
                        }
                    } catch (IOException e) {
                        Logger.error("Error handling client request: " + e.getMessage());
                    }
                });
                clientThread.start();
            }
        } catch (IOException e) {
            Logger.error("Error starting server: " + e.getMessage());
        }
    }

    public void addUser(String username, Socket socket) {
        onlineUsers.put(username, socket);
        Logger.info(username + " has joined the server.");
    }

    public String[] getUsers() {
        return onlineUsers.keySet().toArray(new String[0]);
    }

    public void addMessage(String sender, String recipient, String message) throws IOException {
        Logger.info("Message from " + sender + " to " + recipient + ": " + message);
        Set<String> key = new HashSet<>(Arrays.asList(sender, recipient));
        List<String> messages = messageDatabase.getOrDefault(key, new ArrayList<>());
        messages.add(sender + ": " + message);
        messageDatabase.put(key, messages);
        Socket senderSocket = onlineUsers.get(sender);
        PrintWriter out = new PrintWriter(senderSocket.getOutputStream(), true);
        out.println("ok");
    }

    public String[] getMessages(String sender, String recipient) {
        Logger.info("Getting messages from " + sender + " to " + recipient);
        Set<String> key = new HashSet<>(Arrays.asList(sender, recipient));
        List<String> messages = messageDatabase.getOrDefault(key, new ArrayList<>());
        return messages.toArray(new String[0]);
    }

    public void removeUser(String user, Socket socket) {
        onlineUsers.remove(user, socket);
        Logger.info(user + "has been removed from the server.");
    }

    public String handler(String command, Socket socket) throws IOException {
        String[] tokens = command.split(" ");
        String action = tokens[0];
        if (action.equals("addUser")) {
            String username = tokens[1];
            addUser(username, socket);
        } else if (action.equals("getUsers")) {
            Logger.info("Getting user list");
            String[] users = getUsers();
            ObjectOutputStream objectOut = new ObjectOutputStream(socket.getOutputStream());
            objectOut.writeObject(users);
        } else if (action.equals("addMessage")) {
            String sender = tokens[1];
            String recipient = tokens[2];
            StringBuilder message = new StringBuilder();
            for (int i = 3; i < tokens.length; i++) {
                message.append(tokens[i]).append(" ");
            }
            addMessage(sender, recipient, message.toString());
        } else if (action.equals("getMessages")) {
            String user1 = tokens[1];
            String user2 = tokens[2];
            String[] messages = getMessages(user1, user2);
            ObjectOutputStream objectOut = new ObjectOutputStream(socket.getOutputStream());
            objectOut.writeObject(messages);
        } else if (action.equals("disconnectUser")) {
            String userToRemove = tokens[1];
            removeUser(userToRemove, socket);
        } else {
            Logger.warn("Invalid command: " + command);
            return command;
        }
        return action;
    }
}