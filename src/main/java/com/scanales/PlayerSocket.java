package com.scanales;

import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/player")
@ApplicationScoped
public class PlayerSocket {

    private static final Map<Session, String> sessionIds = new ConcurrentHashMap<>();
    private static final Map<Session, String> displayNames = new ConcurrentHashMap<>();

    public Map<String, String> getSessionInfo() {
        Map<String, String> info = new HashMap<>();
        for (Session session : sessionIds.keySet()) {
            String id = sessionIds.get(session);
            String name = displayNames.getOrDefault(session, "");
            info.put(id, name);
        }
        return info;
    }

    @OnOpen
    public void onOpen(Session session) {
        String uuid = UUID.randomUUID().toString();
        sessionIds.put(session, uuid);
        sendSessionInfo();
        sendMessage(session, "{\"sessionId\":\"" + uuid + "\"}");
    }

    @OnMessage
    public void onMessage(String msg, Session session) {
        if (msg.startsWith("move:")) {
            broadcast(msgToJson(msg));
        } else if (msg.startsWith("name:")) {
            String name = msg.substring(5).trim();
            displayNames.put(session, name);
        }
    }

    @OnClose
    public void onClose(Session session) {
        sessionIds.remove(session);
        displayNames.remove(session);
        sendSessionInfo();
    }

    private void sendSessionInfo() {
        String infoMsg = "{\"type\":\"sessionCount\",\"count\":" + sessionIds.size() + "}";
        broadcast(infoMsg);
    }

    private void broadcast(String msg) {
        for (Session s : sessionIds.keySet()) {
            sendMessage(s, msg);
        }
    }

    private void sendMessage(Session session, String msg) {
        session.getAsyncRemote().sendText(msg);
    }

    private String msgToJson(String msg) {
        try {
            String[] parts = msg.substring(5).split(",");
            int x = Integer.parseInt(parts[0]);
            int y = Integer.parseInt(parts[1]);
            return "{\"x\":" + x + ",\"y\":" + y + "}";
        } catch (Exception e) {
            return "{}";
        }
    }
}
