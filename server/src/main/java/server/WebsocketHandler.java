package server;

import com.google.gson.Gson;
import dataaccess.AuthDao;
import dataaccess.DataAccessException;
import dataaccess.GameDao;
import io.javalin.websocket.WsMessageContext;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import websocket.commands.ConnectCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WebsocketHandler {
    private final Gson gson = new Gson();
    private final GameDao gameDao;
    private final AuthDao authDao;
    private final Map<Integer, List<Session>> sessions = new ConcurrentHashMap<>();

    public WebsocketHandler(GameDao gameDao, AuthDao authDao) {
        this.gameDao = gameDao;
        this.authDao = authDao;
    }

    public void handleMessage(WsMessageContext ctx) {
        UserGameCommand cmd = gson.fromJson(ctx.message(), UserGameCommand.class);
        switch(cmd.getCommandType()) {
            case CONNECT -> {
                connect(ctx, gson.fromJson(ctx.message(), ConnectCommand.class));
            }
            default -> ctx.send("Unknown ws message: " + ctx.message());
        }
    }

    private void connect(WsMessageContext ctx, ConnectCommand cmd) {
        AuthData auth;
        GameData game;
        try {
            auth = authDao.getAuth(cmd.getAuthToken());
            game = gameDao.getGame(cmd.getGameID());
            if (game == null || auth == null) {
                ctx.send(gson.toJson(new ErrorMessage("Error: bad request")));
                return;
            }
            ctx.send(gson.toJson(new LoadGameMessage(gson.toJson(game.game()))));
        } catch (DataAccessException ex) {
            ctx.send(gson.toJson(new ErrorMessage("Error: unable to retrieve game from database")));
            return;
        }

        if (!this.sessions.containsKey(cmd.getGameID())) {
            this.sessions.put(cmd.getGameID(), new ArrayList());
        }
        this.sessions.get(cmd.getGameID()).add(ctx.session);

        String role = "an observer";
        if (game.whiteUsername().equals(auth.username())) {
            role = "white";
        } else if (game.blackUsername().equals(auth.username())) {
            role = "black";
        }

        for (Session s : this.sessions.get(cmd.getGameID())) {
            if (s.isOpen() && s != ctx.session) {
                try {
                    s.getRemote().sendString(gson.toJson(new NotificationMessage(auth.username() + " has joined the game as " + role)));
                } catch (IOException ex) {
                    System.out.println("Something went wrong when notifying a player of a join.");
                }
            }
        }
    }
}
