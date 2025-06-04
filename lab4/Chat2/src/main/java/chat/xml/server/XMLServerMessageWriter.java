package main.java.chat.xml.server;

import main.java.chat.common.ServerMessageWriter;
import main.java.chat.common.ChatUser;
import main.java.chat.ClientHandler;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;

public class XMLServerMessageWriter implements ServerMessageWriter {
    @Override
    public void broadcastMessage(String message, String excludeSession,
                                 Map<String, ClientHandler> sessions, Map<String, ChatUser> users) throws IOException {
        String user = users.get(excludeSession).getName();
        String xmlMessage = "<event name=\"message\">" +
                "<message>" + escapeXml(message) + "</message>" +
                "<name>" + escapeXml(user) + "</name>" +
                "</event>";
        for (Map.Entry<String, ClientHandler> entry : sessions.entrySet()) {
            if (!entry.getKey().equals(excludeSession)) {
                entry.getValue().sendXml(xmlMessage);
            }
        }
    }

    @Override
    public void sendMessage(String sessionId, String message,
                            Map<String, ClientHandler> sessions) throws IOException {
        String xml = "<event name=\"message\">" +
                "<message>" + escapeXml(message) + "</message>" +
                "<name>Сервер</name>" +
                "</event>";
        ClientHandler handler = sessions.get(sessionId);
        if (handler != null) {
            handler.sendXml(xml);
        }
    }

    @Override
    public void broadcastUserEvent(String eventName, String username,
                                   String excludeSession,
                                   Map<String, ClientHandler> sessions,
                                   Map<String, ChatUser> users) throws IOException {
        String xml = "<event name=\"" + eventName + "\"><name>" + escapeXml(username) + "</name></event>";
        for (Map.Entry<String, ClientHandler> entry : sessions.entrySet()) {
            if (!entry.getKey().equals(excludeSession)) {
                entry.getValue().sendXml(xml);
            }
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