package main.java.chat.javaserial.server;

import main.java.chat.common.ChatMessage;
import main.java.chat.common.MessageType;
import main.java.chat.common.ServerMessageWriter;
import main.java.chat.common.ChatUser;
import main.java.chat.ClientHandler;
import java.io.IOException;
import java.util.Map;

public class JavaSerialMessageWriter implements ServerMessageWriter {
    @Override
    public void broadcastMessage(String message, String excludeSession,
                                 Map<String, ClientHandler> sessions, Map<String, ChatUser> users) throws IOException {
        ChatMessage chatMessage = new ChatMessage(users.get(excludeSession).getName(), message, MessageType.MESSAGE);
        for (Map.Entry<String, ClientHandler> entry : sessions.entrySet()) {
            if (!entry.getKey().equals(excludeSession)) {
                entry.getValue().sendMessage(chatMessage);
            }
        }
    }

    @Override
    public void sendMessage(String sessionId, String message,
                            Map<String, ClientHandler> sessions) throws IOException {
        ChatMessage chatMessage = new ChatMessage("Сервер", message, MessageType.MESSAGE);
        ClientHandler handler = sessions.get(sessionId);
        if (handler != null) {
            handler.sendMessage(chatMessage);
        }
    }

    @Override
    public void broadcastUserEvent(String eventName, String username,
                                   String excludeSession,
                                   Map<String, ClientHandler> sessions,
                                   Map<String, ChatUser> users) throws IOException {
        MessageType type = eventName.equals("userlogin") ? MessageType.USER_LOGIN : MessageType.USER_LOGOUT;
        ChatMessage chatMessage = new ChatMessage(username, "", type);
        for (Map.Entry<String, ClientHandler> entry : sessions.entrySet()) {
            if (!entry.getKey().equals(excludeSession)) {
                entry.getValue().sendMessage(chatMessage);
            }
        }
    }
}