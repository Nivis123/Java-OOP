package main.java.chat.xml.client;

import main.java.chat.common.ChatMessage;
import main.java.chat.common.MessageType;
import main.java.chat.common.MessageReader;
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
import java.util.function.Consumer;

public class XMLMessageReader implements MessageReader {
    private final DataOutputStream out;
    private final DataInputStream in;
    private final DocumentBuilder builder;

    public XMLMessageReader(DataOutputStream out, DataInputStream in) throws ParserConfigurationException {
        this.out = out;
        this.in = in;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        this.builder = factory.newDocumentBuilder();
    }

    @Override
    public void readMessages(Consumer<ChatMessage> messageHandler, boolean running) throws IOException {
        try {
            while (running) {
                int length = in.readInt();
                byte[] messageBytes = new byte[length];
                in.readFully(messageBytes);
                String xml = new String(messageBytes, "UTF-8");
                ChatMessage message = parseXml(xml);
                if (message != null) {
                    messageHandler.accept(message);
                }
            }
        } catch (SAXException e) {
            throw new IOException("Ошибка парсинга XML: " + e.getMessage(), e);
        }
    }

    @Override
    public void sendMessage(ChatMessage message) throws IOException {
        String xml;
        switch (message.getType()) {
            case LOGIN:
                xml = "<command name=\"login\">" +
                        "<name>" + escapeXml(message.getSender()) + "</name>" +
                        "<type>CHAT_CLIENT</type>" +
                        "</command>";
                break;
            case LOGOUT:
                xml = "<command name=\"logout\">" +
                        "<session>" + message.getSender() + "</session>" +
                        "</command>";
                break;
            case MESSAGE:
                xml = "<command name=\"message\">" +
                        "<message>" + escapeXml(message.getMessage()) + "</message>" +
                        "<session>" + message.getSender() + "</session>" +
                        "</command>";
                break;
            case USER_LIST:
                xml = "<command name=\"list\">" +
                        "<session>" + message.getSender() + "</session>" +
                        "</command>";
                break;
            default:
                return;
        }
        byte[] bytes = xml.getBytes("UTF-8");
        synchronized (out) {
            out.writeInt(bytes.length);
            out.write(bytes);
            out.flush();
        }
    }

    @Override
    public void close() throws IOException {
        in.close();
        out.close();
    }

    private ChatMessage parseXml(String xml) throws SAXException, IOException {
        Document doc = builder.parse(new ByteArrayInputStream(xml.getBytes("UTF-8")));
        Element root = doc.getDocumentElement();
        String rootName = root.getNodeName();

        if (rootName.equals("event")) {
            String eventName = root.getAttribute("name");
            NodeList nameNodes = root.getElementsByTagName("name");
            NodeList messageNodes = root.getElementsByTagName("message");
            if (eventName.equals("message") && messageNodes.getLength() > 0 && nameNodes.getLength() > 0) {
                return new ChatMessage(nameNodes.item(0).getTextContent(), messageNodes.item(0).getTextContent(), MessageType.MESSAGE);
            } else if (eventName.equals("userlogin") && nameNodes.getLength() > 0) {
                return new ChatMessage(nameNodes.item(0).getTextContent(), "", MessageType.USER_LOGIN);
            } else if (eventName.equals("userlogout") && nameNodes.getLength() > 0) {
                return new ChatMessage(nameNodes.item(0).getTextContent(), "", MessageType.USER_LOGOUT);
            }
        } else if (rootName.equals("success")) {
            NodeList sessionNodes = root.getElementsByTagName("session");
            if (sessionNodes.getLength() > 0) {
                return new ChatMessage("Сервер", sessionNodes.item(0).getTextContent(), MessageType.SUCCESS);
            }
            NodeList listUsers = root.getElementsByTagName("listusers");
            if (listUsers.getLength() > 0) {
                StringBuilder userList = new StringBuilder("Участники чата:\n");
                NodeList users = ((Element) listUsers.item(0)).getElementsByTagName("user");
                for (int i = 0; i < users.getLength(); i++) {
                    Element user = (Element) users.item(i);
                    String name = user.getElementsByTagName("name").item(0).getTextContent();
                    userList.append("- ").append(name).append("\n");
                }
                return new ChatMessage("Сервер", userList.toString(), MessageType.USER_LIST);
            }
        } else if (rootName.equals("error")) {
            NodeList messageNodes = root.getElementsByTagName("message");
            if (messageNodes.getLength() > 0) {
                return new ChatMessage("Сервер", messageNodes.item(0).getTextContent(), MessageType.ERROR);
            }
        }
        return null;
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