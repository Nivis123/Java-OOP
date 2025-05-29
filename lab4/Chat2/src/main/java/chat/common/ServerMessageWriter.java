package main.java.chat.common;

import java.io.IOException;
import java.util.Map;

public interface ServerMessageWriter {
    void broadcastMessage(String message, String excludeSession,
                          Map<String, String> sessions, Map<String, String> users) throws IOException;

    void sendMessage(String sessionId, String message,
                     Map<String, String> sessions) throws IOException;

    void broadcastUserEvent(String eventName, String username,
                            String excludeSession,
                            Map<String, String> sessions,
                            Map<String, String> users) throws IOException;
}