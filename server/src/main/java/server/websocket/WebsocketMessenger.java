package server.websocket;

import chess.ChessGame;
import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;

public class WebsocketMessenger {
    private final Gson gson = new Gson();

    public void sendGame(Session s, ChessGame game) {
        send(s, new LoadGameMessage(gson.toJson(game)));
    }

    public void sendError(Session s, String errmsg) {
        send(s, new ErrorMessage(errmsg));
    }

    public void sendNotification(Session s, String msg) {
        send(s, new NotificationMessage(msg));
    }

    public void send(Session s, ServerMessage msg) {
        try {
            s.getRemote().sendString(gson.toJson(msg));
        } catch (IOException ex) {
            System.out.println("Something went wrong when sending a ws message");
        }
    }
}
