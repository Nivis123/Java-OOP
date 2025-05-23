package main.java.chat.xml.server;

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

public class XMLClientHandler implements Runnable {
    private final Socket clientSocket;
    private final Map<String, String> sessions;
    private final Map<String, String> users;
    private String currentSession;
    private String currentUser;
    private boolean running;

    public XMLClientHandler(Socket socket, Map<String, String> sessions, Map<String, String> users) {
        this.clientSocket = socket;
        this.sessions = sessions;
        this.users = users;
        this.running = true;
    }

    @Override
    public void run() {
        try {
            DataInputStream in = new DataInputStream(clientSocket.getInputStream());
            clientSocket.setSoTimeout(Config.getClientTimeout());

            while (running) {
                int length = in.readInt();
                byte[] messageBytes = new byte[length];
                in.readFully(messageBytes);
                processMessage(new String(messageBytes, "UTF-8"));
            }
        } catch (IOException e) {
            System.err.println("Client error: " + e.getMessage());
        } finally {
            cleanup();
        }
    }

    private void processMessage(String xml) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new ByteArrayInputStream(xml.getBytes("UTF-8")));

            Element root = doc.getDocumentElement();
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
        } catch (ParserConfigurationException | SAXException | IOException e) {
            System.err.println("XML parsing error: " + e.getMessage());
            sendError("Invalid XML message");
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
        sessions.put(currentSession, clientSocket.getInetAddress().getHostAddress() + ":" + clientSocket.getPort());
        users.put(currentSession, username);

        sendSuccess("<session>" + currentSession + "</session>");
        broadcastUserLogin(username);
        System.out.println(username + " joined the chat");
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

        XMLChatServer.broadcastMessage(message, currentSession);
        sendSuccess("");
    }

    private void handleList() throws IOException {
        StringBuilder userList = new StringBuilder("<listusers>");
        for (Map.Entry<String, String> entry : users.entrySet()) {
            userList.append("<user><name>").append(entry.getValue()).append("</name>")
                    .append("<type>CHAT_CLIENT</type></user>");
        }
        userList.append("</listusers>");
        sendSuccess(userList.toString());
    }

    private void sendError(String message) {
        try {
            String xml = "<error><message>" + escapeXml(message) + "</message></error>";
            sendXml(xml);
        } catch (IOException e) {
            System.err.println("Error sending error message: " + e.getMessage());
        }
    }

    private void sendSuccess(String content) throws IOException {
        String xml = "<success>" + content + "</success>";
        sendXml(xml);
    }

    private void sendXml(String xml) throws IOException {
        DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
        byte[] bytes = xml.getBytes("UTF-8");
        out.writeInt(bytes.length);
        out.write(bytes);
        out.flush();
    }

    private void broadcastUserLogin(String username) throws IOException {
        String xml = "<event name=\"userlogin\"><name>" + escapeXml(username) + "</name></event>";
        for (String session : sessions.keySet()) {
            if (!session.equals(currentSession)) {
                XMLChatServer.sendXml(session, xml);
            }
        }
    }

    private void cleanup() {
        if (currentSession != null) {
            sessions.remove(currentSession);
            users.remove(currentSession);
            try {
                broadcastUserLogout(currentUser);
            } catch (IOException e) {
                System.err.println("Error broadcasting logout: " + e.getMessage());
            }
            System.out.println(currentUser + " left the chat");
        }
        try {
            clientSocket.close();
        } catch (IOException e) {
            System.err.println("Error closing socket: " + e.getMessage());
        }
    }

    private void broadcastUserLogout(String username) throws IOException {
        String xml = "<event name=\"userlogout\"><name>" + escapeXml(username) + "</name></event>";
        for (String session : sessions.keySet()) {
            XMLChatServer.sendXml(session, xml);
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