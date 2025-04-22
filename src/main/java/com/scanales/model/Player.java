package com.scanales.model;

public class Player {
    public String sessionId;
    public String name = "";
    public String avatar = "🧑‍💻";
    public String role = "player";
    public int x = 0;
    public int y = 0;

    public Player(String sessionId) {
        this.sessionId = sessionId;
    }
}
