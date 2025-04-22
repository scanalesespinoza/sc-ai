# 🧙‍♂️ Player Tracker RPG

A real-time, multiplayer grid-based player tracker built with **WebSockets**, **Quarkus**, and a clean HTML/JS frontend.

Each player controls an avatar on a 100x100 tile map and can:
- Move to a random position
- Choose avatar and role
- Send chat bubbles or emotes 💬
- See all players live, including direction of movement ↔️↕️
- Leave fading trails when moving 🌀
- Automatically restore their name, role, avatar, and position on refresh

---

## 🚀 Features

- ✅ Real-time updates via WebSocket
- ✅ Auto-reconnect with service status feedback (🟢🟡🔴)
- ✅ Animated movement with jump & direction emojis
- ✅ Persistent player identity with `localStorage`
- ✅ Multiplayer emote/chat bubbles with fade-out
- ✅ Live sessions view via `/sessions.html`
- ✅ Modular `realtime-client.js` for shared logic

---

## 🛠 Technologies

- **Frontend:** HTML, CSS, Vanilla JS
- **Backend:** Java + Quarkus (`jakarta.websocket`)
- **WebSocket API:** `/player` endpoint
- **Live player storage:** in-memory `Map<Session, Player>`

---

## 📦 Project Structure

```
/src/main/java/com/scanales/
│
├── PlayerSocket.java      # WebSocket server
├── Player.java            # Player data model
│
/resources/META-INF/
│
├── resources/static/
│   ├── index.html         # Main game UI
│   ├── sessions.html      # Realtime player list
│   └── realtime-client.js # Shared WebSocket + retry logic
```

---

## 🧪 How to Run

### 1. Clone the repo

```bash
git clone https://github.com/your-username/player-tracker-rpg.git
cd player-tracker-rpg
```

### 2. Run the backend (Quarkus)

Make sure Java is installed (JDK 17+), then:

```bash
./mvnw quarkus:dev
```

This will launch the WebSocket server on:  
`http://localhost:8080`

### 3. Open the frontend

Open [http://localhost:8080/index.html](http://localhost:8080/index.html) in your browser.

You can also open multiple tabs or devices to simulate different players!

### 4. Monitor sessions

Visit [http://localhost:8080/sessions.html](http://localhost:8080/sessions.html)  
to see the list of connected players and their friendly names.

---

## 🧠 Developer Notes

- Player identity is stored client-side using `localStorage`
- Each player gets a `sessionId` assigned by the server on connection
- Messages (like `move:x,y`, `name:`, `say:`, etc.) are simple string commands
- Emotes and text are shown in bubbles and removed after 10 seconds
- Trail effects use animation and class-based styling
- The `realtime-client.js` handles automatic retry, countdown, and queueing

---

## 📸 Screenshots

![Screenshot](./docs/screenshot-demo.png)

---

## 🧩 Future Ideas

- Avatar inventory or card deck system 🃏
- Private chat or whispering between players 🔐
- Mini-map or zoomed view 🔍
- Multiplayer quests or item collection ✨

---

## 👤 Author

Built by [@scanalesespinoza](https://github.com/scanalesespinoza)  
Made with love, emoji, and WebSocket magic ✨

---

## 📄 License

MIT