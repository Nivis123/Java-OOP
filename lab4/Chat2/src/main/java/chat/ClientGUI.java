package main.java.chat;

import main.java.chat.common.ChatMessage;
import main.java.chat.common.MessageType;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

public class ClientGUI extends JFrame {
    private ChatClient client;
    private final JTextArea chatArea;
    private final JTextField inputField;
    private final JTextField usernameField;
    private final JButton connectButton;
    private final JButton sendButton;
    private final JButton userListButton;

    public ClientGUI() {
        setTitle("Chat Client");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel connectionPanel = new JPanel(new FlowLayout());
        usernameField = new JTextField(15);
        connectButton = new JButton("Подключиться");
        connectionPanel.add(new JLabel("Имя:"));
        connectionPanel.add(usernameField);
        connectionPanel.add(connectButton);
        add(connectionPanel, BorderLayout.NORTH);

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(chatArea);
        add(scrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputField = new JTextField();
        inputField.setEnabled(false);
        sendButton = new JButton("Отправить");
        sendButton.setEnabled(false);
        userListButton = new JButton("Пользователи");
        userListButton.setEnabled(false);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(sendButton);
        buttonPanel.add(userListButton);

        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(buttonPanel, BorderLayout.EAST);
        add(inputPanel, BorderLayout.SOUTH);

        connectButton.addActionListener(this::handleConnect);
        sendButton.addActionListener(this::handleSend);
        inputField.addActionListener(this::handleSend);
        userListButton.addActionListener(e -> client.requestUserList());

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (client != null) {
                    client.disconnect();
                }
            }
        });
    }

    private void handleConnect(ActionEvent event) {
        String username = usernameField.getText().trim();
        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Введите имя пользователя", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            client = new ChatClient();
            client.connect("localhost", 5555, username, this::handleMessage);
            usernameField.setEnabled(false);
            connectButton.setEnabled(false);
            inputField.setEnabled(true);
            sendButton.setEnabled(true);
            userListButton.setEnabled(true);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Ошибка подключения: " + e.getMessage(),
                    "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleSend(ActionEvent event) {
        String message = inputField.getText().trim();
        if (!message.isEmpty()) {
            client.sendChatMessage(message);
            inputField.setText("");
        }
    }

    private void handleMessage(ChatMessage message) {
        SwingUtilities.invokeLater(() -> {
            if (message.getType() == MessageType.ERROR) {
                chatArea.append("ОШИБКА: " + message.getMessage() + "\n");
            } else {
                chatArea.append(message.toString() + "\n");
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ClientGUI().setVisible(true));
    }
}