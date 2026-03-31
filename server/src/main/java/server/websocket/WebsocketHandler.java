package server.websocket;

import com.google.gson.Gson;
import dataaccess.AuthDao;
import dataaccess.DataAccessException;
import dataaccess.GameDao;
import io.javalin.websocket.WsMessageContext;
import model.AuthData;
import model.GameData;
import websocket.commands.ConnectCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.NotificationMessage;

public class WebsocketHandler {
    private final Gson gson = new Gson();
    private final GameDao gameDao;
    private final AuthDao authDao;
    private final ConnectionManager connMgr = new ConnectionManager();
    private final WebsocketMessenger msgr = new WebsocketMessenger();

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
        // validate gameID and authToken
        try {
            auth = authDao.getAuth(cmd.getAuthToken());
            game = gameDao.getGame(cmd.getGameID());
        } catch (DataAccessException ex) {
            ctx.send(gson.toJson(new ErrorMessage("Error: unable to retrieve game from database")));
            return;
        }
        if (game == null || auth == null) {
            ctx.send(gson.toJson(new ErrorMessage("Error: bad request")));
            return;
        }

        // send load game message
        msgr.sendGame(ctx.session, game.game());

        // add session to game
        connMgr.add(ctx.session, cmd.getGameID());

        // get player role
        String role = "an observer";
        if (game.whiteUsername().equals(auth.username())) {
            role = "white";
        } else if (game.blackUsername().equals(auth.username())) {
            role = "black";
        }

        connMgr.notifyAll(
                cmd.getGameID(),
                ctx.session,
                new NotificationMessage(auth.username() + " has joined the game as " + role)
        );
    }
}
