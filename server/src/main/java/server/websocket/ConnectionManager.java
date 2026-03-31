package server.websocket;

import chess.ChessGame;
import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    private final Gson gson = new Gson();
    private final WebsocketMessenger msgr = new WebsocketMessenger();
    private final Map<Integer, List<Session>> sessions = new ConcurrentHashMap<>();

    public void add(Session s, int key) {
        if (!sessions.containsKey(key)) {
            sessions.put(key, new ArrayList<>());
        }
        sessions.get(key).add(s);
    }

    public void remove(Session s, int key) {
        sessions.get(key).remove(s);
    }

    public void sendToAll(int key, Session excludedSession, ServerMessage msg) {
        for (Session s : sessions.get(key)) {
            if (s != excludedSession) {
                msgr.send(s, msg);
            }
        }
    }

    public void notifyAll(int key, Session excludedSession, String notification) {
        sendToAll(key, excludedSession, new NotificationMessage(notification));
    }

    public void loadGameAll(int key, Session excludedSession, ChessGame game) {
        sendToAll(key, excludedSession, new LoadGameMessage(gson.toJson(game)));
    }
}
