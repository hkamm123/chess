package server;

import websocket.messages.ServerMessage;

public interface ServerMessageObserver {
    public void notify(ServerMessage message);
}
