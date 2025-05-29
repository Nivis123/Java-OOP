package main.java.chat.javaserial.server;

import main.java.chat.common.ServerMessageWriter;
import main.java.chat.common.ChatMessage;
import main.java.chat.common.MessageType;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class JavaSerialMessageWriter implements ServerMessageWriter {
    @Override
    public void broadcastMessage(String message, String excludeSession,
                                 Map<String, String> sessions, Map<String, String> users) {
    }

    @Override
    public void sendMessage(String sessionId, String message,
                            Map<String, String> sessions) {
    }

    @Override
    public void broadcastUserEvent(String eventName, String username,
                                   String excludeSession,
                                   Map<String, String> sessions,
                                   Map<String, String> users) {
    }
}