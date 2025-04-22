package com.scanales;

import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import com.scanales.model.Player;

import com.google.gson.Gson;

@ServerEndpoint("/player")
@ApplicationScoped
public class PlayerSocket {

    private static final Map<Session, Player> players = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session) {
        String uuid = UUID.randomUUID().toString();
        Player p = new Player(uuid);
        players.put(session, p);

        sendPlayerId(session, uuid);
        broadcastPlayers();
    }

    @OnMessage
    public void onMessage(String msg, Session session) {
        Player p = players.get(session);
        if (p == null) return;

        if (msg.startsWith("move:")) {
            String[] parts = msg.substring(5).split(",");
            p.x = Integer.parseInt(parts[0]);
            p.y = Integer.parseInt(parts[1]);
        } else if (msg.startsWith("name:")) {
            p.name = msg.substring(5).trim();
        } else if (msg.startsWith("avatar:")) {
            p.avatar = msg.substring(7).trim();
        } else if (msg.startsWith("role:")) {
            p.role = msg.substring(5).trim();
        }

        broadcastPlayers();
    }

    @OnClose
    public void onClose(Session session) {
        players.remove(session);
        broadcastPlayers();
    }

    private void sendPlayerId(Session session, String uuid) {
        session.getAsyncRemote().sendText("{\"sessionId\":\"" + uuid + "\"}");
    }

    private void broadcastPlayers() {
        List<Map<String, Object>> list = players.values().stream().map(p -> {
            Map<String, Object> map = new HashMap<>();
            map.put("sessionId", p.sessionId);
            map.put("name", p.name);
            map.put("avatar", p.avatar);
            map.put("role", p.role);
            map.put("x", p.x);
            map.put("y", p.y);
            return map;
        }).toList();

        String json = "{\"type\":\"players\",\"players\":" + new Gson().toJson(list) + "}";
        for (Session s : players.keySet()) {
            s.getAsyncRemote().sendText(json);
        }
    }
}
