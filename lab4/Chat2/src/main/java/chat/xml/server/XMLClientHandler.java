package main.java.chat.xml.server;

import main.java.chat.common.ServerMessageWriter;
import main.java.chat.Config;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class XMLClientHandler implements Runnable {
    private static final Logger logger = Logger.getLogger(XMLClientHandler.class.getName());
    private final Socket clientSocket;
    private final Map<String, String> sessions;
    private final Map<String, String> users;
    private final ServerMessageWriter messageWriter;
    private String currentSession;
    private String currentUser;
    private boolean running;

    public XMLClientHandler(Socket socket, Map<String, String> sessions,
                            Map<String, String> users, ServerMessageWriter messageWriter) {
        this.clientSocket = socket;
        this.sessions = sessions;
        this.users = users;
        this.messageWriter = messageWriter;
        this.running = true;
    }

    @Override
    public void run() {
        try (DataInputStream in = new DataInputStream(clientSocket.getInputStream())) {
            clientSocket.setSoTimeout(Config.getClientTimeout());

            while (running) {
                int length = in.readInt();
                byte[] messageBytes = new byte[length];
                in.readFully(messageBytes);
                String xml = new String(messageBytes, "UTF-8");

                Element root = parseMessage(xml);
                handleCommand(root);
            }
        } catch (IOException e) {
            logger.log(Level.WARNING, "Client error: {0}", e.getMessage());
        } finally {
            cleanup();
        }
    }

    private Element parseMessage(String xml) throws IOException {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new ByteArrayInputStream(xml.getBytes("UTF-8")));
            return doc.getDocumentElement();
        } catch (ParserConfigurationException | SAXException e) {
            logger.log(Level.WARNING, "XML parsing error: {0}", e.getMessage());
            sendError("Invalid XML message");
            throw new IOException("XML parsing error", e);
        }
    }

    private void handleCommand(Element root) throws IOException {
        String commandName = root.getAttribute("name");

        switch (commandName) {
            case "login":
                handleLogin(root);
                break;
            case "logout":
                handleLogout();
                break;
            case "message":
                handleMessage(root);
                break;
            case "list":
                handleList();
                break;
        }
    }

    private void handleLogin(Element root) throws IOException {
        NodeList nameNodes = root.getElementsByTagName("name");
        NodeList typeNodes = root.getElementsByTagName("type");

        if (nameNodes.getLength() == 0 || typeNodes.getLength() == 0) {
            sendError("Missing name or type in login command");
            return;
        }

        String username = nameNodes.item(0).getTextContent();
        String type = typeNodes.item(0).getTextContent();

        if (username.isEmpty()) {
            sendError("Username cannot be empty");
            return;
        }

        currentSession = UUID.randomUUID().toString();
        currentUser = username;

        synchronized (sessions) {
            sessions.put(currentSession, clientSocket.getInetAddress().getHostAddress() + ":" + clientSocket.getPort());
            users.put(currentSession, username);
        }

        sendSuccess("<session>" + currentSession + "</session>");
        messageWriter.broadcastUserEvent("userlogin", username, currentSession, sessions, users);
        logger.log(Level.INFO, "{0} joined the chat", username);
    }

    private void handleLogout() {
        running = false;
    }

    private void handleMessage(Element root) throws IOException {
        NodeList messageNodes = root.getElementsByTagName("message");
        NodeList sessionNodes = root.getElementsByTagName("session");

        if (messageNodes.getLength() == 0 || sessionNodes.getLength() == 0) {
            sendError("Missing message or session in message command");
            return;
        }

        String message = messageNodes.item(0).getTextContent();
        String session = sessionNodes.item(0).getTextContent();

        if (!session.equals(currentSession)) {
            sendError("Invalid session");
            return;
        }

        messageWriter.broadcastMessage(message, currentSession, sessions, users);
        sendSuccess("");
    }

    private void handleList() throws IOException {
        StringBuilder userList = new StringBuilder("<listusers>");
        synchronized (users) {
            for (Map.Entry<String, String> entry : users.entrySet()) {
                userList.append("<user><name>").append(entry.getValue())
                        .append("</name><type>CHAT_CLIENT</type></user>");
            }
        }
        userList.append("</listusers>");
        sendSuccess(userList.toString());
    }

    private void sendError(String message) throws IOException {
        String xml = "<error><message>" + escapeXml(message) + "</message></error>";
        sendXml(xml);
    }

    private void sendSuccess(String content) throws IOException {
        String xml = "<success>" + content + "</success>";
        sendXml(xml);
    }

    private void sendXml(String xml) throws IOException {
        try (DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream())) {
            byte[] bytes = xml.getBytes("UTF-8");
            out.writeInt(bytes.length);
            out.write(bytes);
            out.flush();
        }
    }

    private void cleanup() {
        if (currentSession != null) {
            XMLChatServer.removeClient(currentSession, currentUser);
        }

        try {
            clientSocket.close();
        } catch (IOException e) {
            logger.log(Level.WARNING, "Error closing socket: {0}", e.getMessage());
        }
    }

    private String escapeXml(String input) {
        return input.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }
}