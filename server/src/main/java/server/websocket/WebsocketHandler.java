package server.websocket;

import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.AuthDao;
import dataaccess.DataAccessException;
import dataaccess.GameDao;
import io.javalin.websocket.WsMessageContext;
import model.AuthData;
import model.GameData;
import websocket.commands.ConnectCommand;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

import static chess.ChessGame.TeamColor.BLACK;
import static chess.ChessGame.TeamColor.WHITE;

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
        switch (cmd.getCommandType()) {
            case CONNECT -> {
                connect(ctx, gson.fromJson(ctx.message(), ConnectCommand.class));
            }
            case MAKE_MOVE -> {
                handleMakeMove(ctx, gson.fromJson(ctx.message(), MakeMoveCommand.class));
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

        connMgr.notifyAll(cmd.getGameID(), ctx.session, new NotificationMessage(auth.username() + " has joined the game as " + role));
    }

    private void handleMakeMove(WsMessageContext ctx, MakeMoveCommand cmd) {
        // validate gameID and authToken
        GameData game;
        AuthData auth;
        try {
            auth = authDao.getAuth(cmd.getAuthToken());
            game = gameDao.getGame(cmd.getGameID());
            if (auth == null || game == null) {
                msgr.sendError(ctx.session, "Error: bad request");
                return;
            }

            if ((game.game().getTeamTurn() == WHITE && !(auth.username().equals(game.whiteUsername()))) ||
                    (game.game().getTeamTurn() == BLACK && !(auth.username().equals(game.blackUsername())))) {
                msgr.sendError(ctx.session, "Error: out of turn");
                return;
            }

            // make move, send error message if invalid
            game.game().makeMove(cmd.getMove());
            // update game in db
            gameDao.updateGame(game);
        } catch (InvalidMoveException ex) {
            msgr.sendError(ctx.session, "Error: invalid move");
            return;
        } catch (DataAccessException e) {
            msgr.sendError(ctx.session, "There was an unexpected error.");
            return;
        }

        // notify all other clients of move made
        String msg = auth.username() + " made move: " + cmd.getMove().toString();
        connMgr.notifyAll(cmd.getGameID(), ctx.session, new NotificationMessage(msg));

        // send load_game to all clients, and notify all clients of check/checkmate/stalemate
        String gameStatusChangeMsg = "";
        if (game.game().isInCheck(WHITE)) {
            gameStatusChangeMsg = game.whiteUsername() + " is in check.";
        } else if (game.game().isInCheck(BLACK)) {
            gameStatusChangeMsg = game.blackUsername() + " is in check.";
        }

        if (game.game().isInCheckmate(WHITE)) {
            gameStatusChangeMsg = game.whiteUsername() + " is in checkmate!";
        } else if (game.game().isInCheckmate(BLACK)) {
            gameStatusChangeMsg = game.blackUsername() + " is in checkmate!";
        } else if (game.game().isInStalemate(BLACK)) {
            gameStatusChangeMsg = "Stalemate!";
        }

        connMgr.notifyAll(cmd.getGameID(), null, new LoadGameMessage(gson.toJson(game.game())));
        if (!gameStatusChangeMsg.isEmpty()) {
            connMgr.notifyAll(cmd.getGameID(), null, new NotificationMessage(gameStatusChangeMsg));
        }
    }
}
