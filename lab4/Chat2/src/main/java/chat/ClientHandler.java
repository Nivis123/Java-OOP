package main.java.chat;

import main.java.chat.common.ChatMessage;
import main.java.chat.common.ChatUser;
import main.java.chat.common.MessageType;
import main.java.chat.common.ServerMessageWriter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.Socket;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientHandler implements Runnable {
    private static final Logger logger = Logger.getLogger(ClientHandler.class.getName());
    private final Socket clientSocket;
    private final Map<String, ClientHandler> clients;
    private final Map<String, ChatUser> users;
    private final ServerMessageWriter messageWriter;
    private ObjectOutputStream objOut;
    private ObjectInputStream objIn;
    private DataOutputStream xmlOut;
    private DataInputStream xmlIn;
    private ChatUser currentUser;
    private final String sessionId;
    private boolean running;
    private final DocumentBuilder builder;

    public ClientHandler(Socket socket, Map<String, ClientHandler> clients, Map<String, ChatUser> users, ServerMessageWriter messageWriter) {
        this.clientSocket = socket;
        this.clients = clients;
        this.users = users;
        this.messageWriter = messageWriter;
        this.sessionId = UUID.randomUUID().toString();
        this.running = true;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            this.builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException("Ошибка инициализации XML-парсера", e);
        }
    }

    public String getSessionId() {
        return sessionId;
    }

    @Override
    public void run() {
        try {
            clientSocket.setSoTimeout(Config.getClientTimeout());
            if (Config.useXmlProtocol()) {
                xmlOut = new DataOutputStream(clientSocket.getOutputStream());
                xmlIn = new DataInputStream(clientSocket.getInputStream());
            } else {
                objOut = new ObjectOutputStream(clientSocket.getOutputStream());
                objIn = new ObjectInputStream(clientSocket.getInputStream());
            }

            while (running) {
                if (Config.useXmlProtocol()) {
                    int length = xmlIn.readInt();
                    byte[] messageBytes = new byte[length];
                    xmlIn.readFully(messageBytes);
                    String xml = new String(messageBytes, "UTF-8");
                    processMessage(parseXml(xml));
                } else {
                    Object input = objIn.readObject();
                    if (input instanceof ChatMessage) {
                        processMessage((ChatMessage) input);
                    }
                }
            }
        } catch (IOException | ClassNotFoundException | SAXException e) {
            logger.log(Level.WARNING, "Client error: {0}", e.getMessage());
            try {
                sendMessage(new ChatMessage("Сервер", "Ошибка обработки сообщения: " + e.getMessage(), MessageType.ERROR));
            } catch (IOException ignored) {
                // Игнорируем, так как соединение может быть уже закрыто
            }
        } finally {
            cleanup();
        }
    }

    private ChatMessage parseXml(String xml) throws SAXException, IOException {
        Document doc = builder.parse(new ByteArrayInputStream(xml.getBytes("UTF-8")));
        Element root = doc.getDocumentElement();
        String commandName = root.getAttribute("name");

        switch (commandName) {
            case "login":
                NodeList nameNodes = root.getElementsByTagName("name");
                if (nameNodes.getLength() > 0) {
                    return new ChatMessage(nameNodes.item(0).getTextContent(), "", MessageType.LOGIN);
                }
                break;
            case "logout":
                NodeList sessionNodes = root.getElementsByTagName("session");
                if (sessionNodes.getLength() > 0) {
                    return new ChatMessage(sessionNodes.item(0).getTextContent(), "", MessageType.LOGOUT);
                }
                break;
            case "message":
                NodeList messageNodes = root.getElementsByTagName("message");
                sessionNodes = root.getElementsByTagName("session");
                if (messageNodes.getLength() > 0 && sessionNodes.getLength() > 0) {
                    return new ChatMessage(sessionNodes.item(0).getTextContent(), messageNodes.item(0).getTextContent(), MessageType.MESSAGE);
                }
                break;
            case "list":
                sessionNodes = root.getElementsByTagName("session");
                if (sessionNodes.getLength() > 0) {
                    return new ChatMessage(sessionNodes.item(0).getTextContent(), "", MessageType.USER_LIST);
                }
                break;
        }
        return new ChatMessage("Сервер", "Неверная команда", MessageType.ERROR);
    }

    private void processMessage(ChatMessage message) throws IOException {
        switch (message.getType()) {
            case LOGIN:
                handleLogin(message);
                break;
            case LOGOUT:
                handleLogout();
                break;
            case MESSAGE:
                handleMessage(message);
                break;
            case USER_LIST:
                handleList();
                break;
        }
    }

    private void handleLogin(ChatMessage message) throws IOException {
        String username = message.getSender();
        if (username == null || username.trim().isEmpty()) {
            sendMessage(new ChatMessage("Сервер", "Имя пользователя не может быть пустым", MessageType.ERROR));
            return;
        }
        currentUser = new ChatUser(username, sessionId);
        synchronized (users) {
            users.put(sessionId, currentUser);
        }
        sendMessage(new ChatMessage("Сервер", "session:" + sessionId, MessageType.SUCCESS));
        messageWriter.broadcastUserEvent("userlogin", username, sessionId, clients, users);
        logger.log(Level.INFO, "{0} joined the chat", username);
    }

    private void handleLogout() {
        running = false;
    }

    private void handleMessage(ChatMessage message) throws IOException {
        if (!message.getSender().equals(sessionId)) {
            sendMessage(new ChatMessage("Сервер", "Неверная сессия", MessageType.ERROR));
            return;
        }
        messageWriter.broadcastMessage(message.getMessage(), sessionId, clients, users);
        sendMessage(new ChatMessage("Сервер", "", MessageType.SUCCESS));
    }

    private void handleList() throws IOException {
        StringBuilder userList = new StringBuilder();
        if (Config.useXmlProtocol()) {
            userList.append("<listusers>");
            synchronized (users) {
                for (ChatUser user : users.values()) {
                    userList.append("<user><name>").append(escapeXml(user.getName()))
                            .append("</name><type>CHAT_CLIENT</type></user>");
                }
            }
            userList.append("</listusers>");
            sendMessage(new ChatMessage("Сервер", userList.toString(), MessageType.SUCCESS));
        } else {
            userList.append("Участники чата:\n");
            synchronized (users) {
                for (ChatUser user : users.values()) {
                    userList.append("- ").append(user.getName()).append("\n");
                }
            }
            sendMessage(new ChatMessage("Сервер", userList.toString(), MessageType.USER_LIST));
        }
    }

    public void sendMessage(ChatMessage message) throws IOException {
        if (Config.useXmlProtocol()) {
            String xml;
            switch (message.getType()) {
                case SUCCESS:
                    xml = "<success>" + (message.getMessage().startsWith("session:") ?
                            "<session>" + message.getMessage().substring(8) + "</session>" :
                            message.getMessage()) + "</success>";
                    break;
                case ERROR:
                    xml = "<error><message>" + escapeXml(message.getMessage()) + "</message></error>";
                    break;
                default:
                    xml = "<event name=\"" + (message.getType() == MessageType.USER_LOGIN ? "userlogin" :
                            message.getType() == MessageType.USER_LOGOUT ? "userlogout" : "message") + "\">" +
                            "<name>" + escapeXml(message.getSender()) + "</name>" +
                            (message.getType() == MessageType.MESSAGE ? "<message>" + escapeXml(message.getMessage()) + "</message>" : "") +
                            "</event>";
                    break;
            }
            sendXml(xml);
        } else {
            synchronized (objOut) {
                objOut.writeObject(message);
                objOut.flush();
            }
        }
    }

    public void sendXml(String xml) throws IOException {
        synchronized (xmlOut) {
            byte[] bytes = xml.getBytes("UTF-8");
            xmlOut.writeInt(bytes.length);
            xmlOut.write(bytes);
            xmlOut.flush();
        }
    }

    private void cleanup() {
        if (currentUser != null) {
            ChatServer.removeClient(this, currentUser);
            try {
                messageWriter.broadcastUserEvent("userlogout", currentUser.getName(), sessionId, clients, users);
            } catch (IOException e) {
                logger.log(Level.WARNING, "Error broadcasting logout: {0}", e.getMessage());
            }
        }
        try {
            clientSocket.close();
        } catch (IOException e) {
            logger.log(Level.WARNING, "Error closing socket: {0}", e.getMessage());
        }
    }

    private String escapeXml(String input) {
        if (input == null) return "";
        return input.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }
}