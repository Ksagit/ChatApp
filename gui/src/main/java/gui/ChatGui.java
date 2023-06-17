package gui;

import java.awt.*;
import java.io.IOException;
import javax.swing.*;

public class ChatGui extends JFrame {

    private final JTextField mesField;
    private final JTextArea chatHistory;
    private final JTextArea searchHistory;
    private final JTextArea userList;
    private final JTextField userNick;
    private final JTextField recipientNick;

    public ChatGui(Client client) {
        JButton sendButton = new JButton("Send");
        JButton searchButton = new JButton("Search");
        JButton getUsers = new JButton("Refresh user list");
        JButton nickButton = new JButton("Enter");
        JButton getMessages = new JButton("Receive");
        JButton disconnectButton = new JButton("Disconnect");

        chatHistory = new JTextArea(10, 30);
        chatHistory.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        searchHistory = new JTextArea(10, 30);
        searchHistory.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        userList = new JTextArea(20, 10);
        userList.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        mesField = new JTextField(20);
        userNick = new JTextField(10);
        recipientNick = new JTextField(10);

        chatHistory.setEditable(false);
        searchHistory.setEditable(false);
        userList.setEditable(false);

        JLabel chatHistoryLabel = new JLabel("Chat");
        JLabel searchHistoryLabel = new JLabel("Search");
        JLabel mesFieldLabel = new JLabel("Input");
        JLabel userListLabel = new JLabel("Users");
        JLabel userNickLabel = new JLabel("Nick");
        JLabel recipientNickLabel = new JLabel("Recipient");

        JPanel userListPanel = new JPanel(new BorderLayout());
        userListPanel.add(userList);
        userListPanel.add(userListLabel, BorderLayout.NORTH);

        JPanel chatHistoryPanel = new JPanel(new BorderLayout());
        chatHistoryPanel.add(chatHistory);
        chatHistoryPanel.add(chatHistoryLabel, BorderLayout.NORTH);

        JPanel searchHistoryPanel = new JPanel(new BorderLayout());
        searchHistoryPanel.add(searchHistory);
        searchHistoryPanel.add(searchHistoryLabel, BorderLayout.NORTH);

        JPanel mesFieldPanel = new JPanel(new BorderLayout());
        mesFieldPanel.add(mesField);
        mesFieldPanel.add(mesFieldLabel, BorderLayout.NORTH);

        JPanel userNickPanel = new JPanel(new BorderLayout());
        userNickPanel.add(userNick);
        userNickPanel.add(userNickLabel, BorderLayout.NORTH);

        JPanel recipientPanel = new JPanel(new BorderLayout());
        recipientPanel.add(recipientNick);
        recipientPanel.add(recipientNickLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.add(disconnectButton, BorderLayout.SOUTH);
        buttonPanel.add(searchButton, BorderLayout.NORTH);
        buttonPanel.add(sendButton, BorderLayout.CENTER);
        buttonPanel.add(getUsers, BorderLayout.EAST);
        buttonPanel.add(getMessages, BorderLayout.WEST);

        JPanel connectionPanel = new JPanel(new BorderLayout());
        connectionPanel.add(userNickPanel, BorderLayout.WEST);
        connectionPanel.add(recipientPanel, BorderLayout.CENTER);
        connectionPanel.add(nickButton, BorderLayout.SOUTH);

        JPanel topPart = new JPanel(new BorderLayout());
        topPart.add(connectionPanel);

        JPanel midPart = new JPanel(new BorderLayout());
        midPart.add(userListPanel, BorderLayout.WEST);
        midPart.add(chatHistoryPanel, BorderLayout.CENTER);
        midPart.add(searchHistoryPanel, BorderLayout.EAST);
        midPart.add(mesFieldPanel, BorderLayout.SOUTH);

        JPanel botPart = new JPanel(new BorderLayout());
        botPart.add(mesFieldPanel, BorderLayout.NORTH);
        botPart.add(buttonPanel, BorderLayout.SOUTH);

        JPanel chatWindow = new JPanel(new BorderLayout());
        chatWindow.add(topPart, BorderLayout.NORTH);
        chatWindow.add(midPart, BorderLayout.CENTER);
        chatWindow.add(botPart, BorderLayout.SOUTH);

        add(chatWindow);

        sendButton.addActionListener(e -> {
            try {
                client.sendMessage(recipientNick.getText(), mesField.getText(), chatHistory);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        searchButton.addActionListener(e -> {
            try {
                client.searchMessage(recipientNick.getText(), mesField.getText(), searchHistory);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        getUsers.addActionListener(e -> client.refreshUsers(userList));

        nickButton.addActionListener(e -> client.joinServer(userNick.getText()));

        disconnectButton.addActionListener(e -> client.disconnectUser(userNick.getText()));

        getMessages.addActionListener(e -> {
            try {
                client.refreshMessages(chatHistory, recipientNick.getText());
            } catch (IOException | ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        });



        setTitle("mychat");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setVisible(true);
    }
}