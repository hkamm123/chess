package server.websocket;

import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
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

    public void notifyAll(int key, Session excludedSession, ServerMessage msg) {
        for (Session s : sessions.get(key)) {
            if (s != excludedSession) {
                msgr.send(s, msg);
            }
        }
    }
}
