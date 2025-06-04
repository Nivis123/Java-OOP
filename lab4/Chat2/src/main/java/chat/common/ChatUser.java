package main.java.chat.common;

import java.io.Serializable;

public class ChatUser implements Serializable {
    private String name;
    private String sessionId;

    public ChatUser(String name, String sessionId) {
        this.name = name;
        this.sessionId = sessionId;
    }

    public String getName() {
        return name;
    }

    public String getSessionId() {
        return sessionId;
    }

    @Override
    public String toString() {
        return name;
    }
}