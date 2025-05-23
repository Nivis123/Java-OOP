package main.java.chat.javaserial.client;

import main.java.chat.common.ChatMessage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JavaSerialClientGUI extends JFrame {
    private JavaSerialChatClient client;
    private final JTextArea chatArea;
    private final JTextField inputField;
    private final JTextField usernameField;
    private final JButton connectButton;
    private final JButton sendButton;
    private final List<ChatMessage> messageHistory = new ArrayList<>();

    public JavaSerialClientGUI() {
        setTitle("Java Serial Chat Client");
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
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        add(inputPanel, BorderLayout.SOUTH);

        connectButton.addActionListener(this::handleConnect);
        sendButton.addActionListener(this::handleSend);
        inputField.addActionListener(this::handleSend);

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
            client = new JavaSerialChatClient();
            client.connect("localhost", 5555, username, this::handleMessage);

            usernameField.setEnabled(false);
            connectButton.setEnabled(false);
            inputField.setEnabled(true);
            sendButton.setEnabled(true);
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
            messageHistory.add(message);
            chatArea.append(message.toString() + "\n");
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JavaSerialClientGUI gui = new JavaSerialClientGUI();
            gui.setVisible(true);
        });
    }
}