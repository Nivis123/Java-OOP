package main.java.chat.xml.client;

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
import java.util.function.Consumer;

public class XMLChatClient {
    private String sessionId;
    private Socket socket;
    private DataOutputStream out;
    private DataInputStream in;
    private Consumer<String> messageHandler;
    private boolean running;
    private Thread receiverThread;

    public void connect(String host, int port, String username, Consumer<String> messageHandler) throws IOException {
        this.messageHandler = messageHandler;
        this.socket = new Socket(host, port);
        this.out = new DataOutputStream(socket.getOutputStream());
        this.in = new DataInputStream(socket.getInputStream());
        this.running = true;

        String loginXml = "<command name=\"login\">" +
                "<name>" + escapeXml(username) + "</name>" +
                "<type>CHAT_CLIENT</type>" +
                "</command>";
        sendXml(loginXml);

        receiverThread = new Thread(this::receiveMessages);
        receiverThread.start();
    }

    public void disconnect() {
        running = false;
        try {
            if (sessionId != null) {
                String logoutXml = "<command name=\"logout\">" +
                        "<session>" + sessionId + "</session>" +
                        "</command>";
                sendXml(logoutXml);
            }
            socket.close();
            receiverThread.join();
        } catch (IOException | InterruptedException e) {
            System.err.println("Error disconnecting: " + e.getMessage());
        }
    }

    public void sendChatMessage(String message) {
        if (sessionId != null) {
            String messageXml = "<command name=\"message\">" +
                    "<message>" + escapeXml(message) + "</message>" +
                    "<session>" + sessionId + "</session>" +
                    "</command>";
            sendXml(messageXml);
        }
    }

    public void requestUserList() {
        if (sessionId != null) {
            String listXml = "<command name=\"list\">" +
                    "<session>" + sessionId + "</session>" +
                    "</command>";
            sendXml(listXml);
        }
    }

    private void sendXml(String xml) {
        try {
            byte[] bytes = xml.getBytes("UTF-8");
            out.writeInt(bytes.length);
            out.write(bytes);
            out.flush();
        } catch (IOException e) {
            System.err.println("Error sending message: " + e.getMessage());
        }
    }

    private void receiveMessages() {
        try {
            while (running) {
                int length = in.readInt();
                byte[] messageBytes = new byte[length];
                in.readFully(messageBytes);
                String xml = new String(messageBytes, "UTF-8");
                processMessage(xml);
            }
        } catch (IOException e) {
            if (running) {
                System.err.println("Error receiving message: " + e.getMessage());
            }
        }
    }

    private void processMessage(String xml) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new ByteArrayInputStream(xml.getBytes("UTF-8")));

            Element root = doc.getDocumentElement();
            String rootName = root.getNodeName();

            if (rootName.equals("success")) {
                NodeList sessionNodes = root.getElementsByTagName("session");
                if (sessionNodes.getLength() > 0) {
                    sessionId = sessionNodes.item(0).getTextContent();
                }
                messageHandler.accept(xml);
            } else if (rootName.equals("error")) {
                NodeList messageNodes = root.getElementsByTagName("message");
                if (messageNodes.getLength() > 0) {
                    System.err.println("Error: " + messageNodes.item(0).getTextContent());
                }
            } else {
                messageHandler.accept(xml);
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            System.err.println("XML parsing error: " + e.getMessage());
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