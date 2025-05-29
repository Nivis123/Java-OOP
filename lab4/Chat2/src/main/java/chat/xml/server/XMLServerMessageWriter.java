package main.java.chat.xml.server;

import main.java.chat.common.ServerMessageWriter;
import java.io.IOException;
import java.util.Map;

public class XMLServerMessageWriter implements ServerMessageWriter {
    @Override
    public void broadcastMessage(String message, String excludeSession,
                                 Map<String, String> sessions, Map<String, String> users) throws IOException {
        String user = users.get(excludeSession);
        String xmlMessage = "<event name=\"message\">" +
                "<message>" + escapeXml(message) + "</message>" +
                "<name>" + escapeXml(user) + "</name>" +
                "</event>";

        for (Map.Entry<String, String> entry : sessions.entrySet()) {
            if (!entry.getKey().equals(excludeSession)) {
                sendMessage(entry.getKey(), xmlMessage, sessions);
            }
        }
    }

    @Override
    public void sendMessage(String sessionId, String xml,
                            Map<String, String> sessions) throws IOException {
        XMLChatServer.sendXml(sessionId, xml);
    }

    @Override
    public void broadcastUserEvent(String eventName, String username,
                                   String excludeSession,
                                   Map<String, String> sessions,
                                   Map<String, String> users) throws IOException {
        String xml = "<event name=\"" + eventName + "\"><name>" + escapeXml(username) + "</name></event>";
        for (Map.Entry<String, String> entry : sessions.entrySet()) {
            if (!entry.getKey().equals(excludeSession)) {
                sendMessage(entry.getKey(), xml, sessions);
            }
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