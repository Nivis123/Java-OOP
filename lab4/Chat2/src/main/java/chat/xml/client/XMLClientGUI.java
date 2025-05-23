package main.java.chat.xml.client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import java.io.ByteArrayInputStream;

public class XMLClientGUI extends JFrame {
    private XMLChatClient client;
    private final JTextArea chatArea;
    private final JTextField inputField;
    private final JTextField usernameField;
    private final JButton connectButton;
    private final JButton sendButton;
    private final JButton userListButton;

    public XMLClientGUI() {
        setTitle("XML Chat Client");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel connectionPanel = new JPanel(new FlowLayout());
        usernameField = new JTextField(15);
        connectButton = new JButton("Connect");
        connectionPanel.add(new JLabel("Username:"));
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
        sendButton = new JButton("Send");
        sendButton.setEnabled(false);
        userListButton = new JButton("Users");
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
            JOptionPane.showMessageDialog(this, "Enter username", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            client = new XMLChatClient();
            client.connect("localhost", 5555, username, this::handleMessage);

            usernameField.setEnabled(false);
            connectButton.setEnabled(false);
            inputField.setEnabled(true);
            sendButton.setEnabled(true);
            userListButton.setEnabled(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Connection error: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleSend(ActionEvent event) {
        String message = inputField.getText().trim();
        if (!message.isEmpty()) {
            client.sendChatMessage(message);
            inputField.setText("");
        }
    }

    private void handleMessage(String xml) {
        SwingUtilities.invokeLater(() -> {
            try {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document doc = builder.parse(new ByteArrayInputStream(xml.getBytes("UTF-8")));

                Element root = doc.getDocumentElement();
                String rootName = root.getNodeName();

                if (rootName.equals("event")) {
                    String eventName = root.getAttribute("name");
                    if (eventName.equals("message")) {
                        NodeList messageNodes = root.getElementsByTagName("message");
                        NodeList nameNodes = root.getElementsByTagName("name");
                        if (messageNodes.getLength() > 0 && nameNodes.getLength() > 0) {
                            chatArea.append(nameNodes.item(0).getTextContent() + ": " +
                                    messageNodes.item(0).getTextContent() + "\n");
                        }
                    } else if (eventName.equals("userlogin")) {
                        NodeList nameNodes = root.getElementsByTagName("name");
                        if (nameNodes.getLength() > 0) {
                            chatArea.append(">>> " + nameNodes.item(0).getTextContent() + " joined\n");
                        }
                    } else if (eventName.equals("userlogout")) {
                        NodeList nameNodes = root.getElementsByTagName("name");
                        if (nameNodes.getLength() > 0) {
                            chatArea.append("<<< " + nameNodes.item(0).getTextContent() + " left\n");
                        }
                    }
                } else if (rootName.equals("success")) {
                    NodeList listUsers = root.getElementsByTagName("listusers");
                    if (listUsers.getLength() > 0) {
                        chatArea.append("Online users:\n");
                        NodeList users = ((Element)listUsers.item(0)).getElementsByTagName("user");
                        for (int i = 0; i < users.getLength(); i++) {
                            Element user = (Element)users.item(i);
                            String name = user.getElementsByTagName("name").item(0).getTextContent();
                            chatArea.append("- " + name + "\n");
                        }
                    }
                } else if (rootName.equals("error")) {
                    NodeList messageNodes = root.getElementsByTagName("message");
                    if (messageNodes.getLength() > 0) {
                        chatArea.append("ERROR: " + messageNodes.item(0).getTextContent() + "\n");
                    }
                }
            } catch (Exception e) {
                chatArea.append("Error parsing message: " + e.getMessage() + "\n");
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            XMLClientGUI gui = new XMLClientGUI();
            gui.setVisible(true);
        });
    }
}