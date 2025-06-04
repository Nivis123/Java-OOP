package main.java.chat.common;

import main.java.chat.ClientHandler;

import java.io.IOException;
import java.util.Map;

public interface ServerMessageWriter {
    void broadcastMessage(String message, String excludeSession,
                          Map<String, ClientHandler> sessions, Map<String, ChatUser> users) throws IOException;

    void sendMessage(String sessionId, String message,
                     Map<String, ClientHandler> sessions) throws IOException;

    void broadcastUserEvent(String eventName, String username,
                            String excludeSession,
                            Map<String, ClientHandler> sessions,
                            Map<String, ChatUser> users) throws IOException;
}