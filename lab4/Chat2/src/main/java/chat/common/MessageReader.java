package main.java.chat.common;

import java.io.IOException;
import java.util.function.Consumer;

public interface MessageReader {
    void readMessages(Consumer<ChatMessage> messageHandler, boolean running) throws IOException;
    void sendMessage(ChatMessage message) throws IOException;
    void close() throws IOException;
}