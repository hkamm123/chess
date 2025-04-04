package server;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.*;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import service.GameService;
import service.UserService;
import spark.*;
import spark.Request;
import websocket.commands.*;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import javax.websocket.Endpoint;
import java.io.IOException;
import java.util.*;

import static chess.ChessGame.TeamColor.BLACK;
import static chess.ChessGame.TeamColor.WHITE;
import static dataaccess.UserDao.BAD_REQUEST_ERR_MSG;
import static dataaccess.UserDao.USER_TAKEN_ERR_MSG;
import static service.UserService.UNAUTHORIZED_ERR_MSG;

@WebSocket
public class Server {
    private final Gson serializer = new Gson();
    private final UserDao userDao;
    private final AuthDao authDao;
    private final GameDao gameDao;
    private final UserService userService;
    private final GameService gameService;
    private final HashMap<Integer, Set<Session>> sessions;

    public Server() {
        try {
            userDao = new MySQLUserDao();
            authDao = new MySQLAuthDao();
            gameDao = new MySQLGameDao();
            userService = new UserService(userDao, authDao);
            gameService = new GameService(gameDao, authDao);
            sessions = new HashMap<>();
        } catch (DataAccessException ex) {
            throw new RuntimeException("Error initializing server: " + ex.getMessage());
        }
    }

    private String serialize(Object object) {
        return serializer.toJson(object);
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        Spark.webSocket("/ws", Server.class);
        // Register your endpoints and handle exceptions here.
        Spark.post("/user", this::register);
        Spark.delete("/db", this::clear);
        Spark.post("/session", this::login);
        Spark.delete("/session", this::logout);
        Spark.get("/game", this::listGames);
        Spark.post("/game", this::createGame);
        Spark.put("/game", this::joinGame);

        Spark.awaitInitialization();
        return Spark.port();
    }

    private Object clear(Request request, Response response) {
        try {
            gameService.clear();
            userService.clear();
        } catch (DataAccessException ex) {
            response.status(500);
            response.body(serialize(new LogoutResult(ex.getMessage())));
        }
        response.status(200);
        response.body("{}");
        return response.body();
    }

    private int getStatus(String message) {
        return switch (message) {
            case null -> 200;
            case USER_TAKEN_ERR_MSG -> 403;
            case BAD_REQUEST_ERR_MSG -> 400;
            case UNAUTHORIZED_ERR_MSG -> 401;
            default -> 500;
        };
    }

    private Object register(Request request, Response response) throws DataAccessException {
        RegisterRequest regReq = serializer.fromJson(request.body(), RegisterRequest.class);
        RegisterResult result = userService.register(regReq);
        response.status(getStatus(result.message()));
        response.body(serialize(result));
        return response.body();
    }

    private Object login(Request request, Response response) {
        LoginRequest loginReq = serializer.fromJson(request.body(), LoginRequest.class);
        RegisterResult result = userService.login(loginReq);
        response.status(getStatus(result.message()));
        response.body(serialize(result));
        return response.body();
    }

    private Object logout(Request request, Response response) {
        String authToken = request.headers("Authorization");
        LogoutResult result = userService.logout(authToken);
        response.status(getStatus(result.message()));
        response.body(serialize(result));
        return response.body();
    }

    private Object listGames(Request request, Response response) {
        String authToken = request.headers("Authorization");
        ListResult result = gameService.listGames(authToken);
        response.status(getStatus(result.message()));
        response.body(serialize(result));
        return response.body();
    }

    private Object createGame(Request request, Response response) {
        CreateRequest createReq = serializer.fromJson(request.body(), CreateRequest.class);
        String authToken = request.headers("Authorization");
        CreateResult result = gameService.createGame(createReq, authToken);
        response.status(getStatus(result.message()));
        response.body(serialize(result));
        return response.body();
    }

    private Object joinGame(Request request, Response response) {
        String authToken = request.headers("Authorization");
        JoinRequest joinReq = serializer.fromJson(request.body(), JoinRequest.class);
        JoinResult result = gameService.joinGame(joinReq, authToken);
        response.status(getStatus(result.message()));
        response.body(serialize(result));
        return response.body();
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws Exception {
        try {
            UserGameCommand command = serializer.fromJson(message, UserGameCommand.class);

            String username = authDao.getUsername(command.getAuthToken());

            saveSession(command.getGameID(), session);

            switch (command.getCommandType()) {
                case CONNECT -> connect(session, username, (ConnectCommand) serializer.fromJson(message, ConnectCommand.class));
                case MAKE_MOVE -> makeMove(session, username, (MakeMoveCommand) serializer.fromJson(message, MakeMoveCommand.class));
                case LEAVE -> leaveGame(session, username, (LeaveCommand) serializer.fromJson(message, LeaveCommand.class));
                case RESIGN -> resign(session, username, (ResignCommand) serializer.fromJson(message, ResignCommand.class));
            }
        } catch (Exception ex) {
            sendMessage(session, new ErrorMessage("Error: " + ex.getMessage()));
        }
    }

    private void saveSession(int gameID, Session session) {
        if (sessions.containsKey(gameID)) {
            sessions.get(gameID).add(session);
        } else {
            sessions.put(gameID, new HashSet<>());
            sessions.get(gameID).add(session);
        }
    }

    private void removeSession(int gameID, Session session) {
        if (sessions.containsKey(gameID)) {
            sessions.get(gameID).remove(session);
        }
    }

    private void sendMessage(Session session, ServerMessage message) {
        try {
            session.getRemote().sendString((serializer.toJson(message)));
        } catch (IOException ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    private void makeMove(Session session, String username, MakeMoveCommand command) {
        try {
            ChessGame updatedGame = gameService.makeMove(command.getGameID(), command.getAuthToken(), command.getMove());

            for (Session s : sessions.get(command.getGameID())) {
                sendMessage(s, new LoadGameMessage(serializer.toJson(updatedGame)));
                if (s != session) {
                    sendMessage(s, new NotificationMessage(username + " made move " + command.getMove().toString()));
                }
            }
            checkForDanger(command.getGameID(), updatedGame);
        } catch (Exception ex) {
            sendMessage(session, new ErrorMessage(ex.getMessage())); // TODO: handle this better if needed
        }
    }

    private void checkForDanger(int gameID, ChessGame game) throws Exception {
        // TODO: implement
        // somehow get the username of the white and black players
        String[] usernames = gameService.getUsernames(gameID);
        String whiteUsername = usernames[0];
        String blackUsername = usernames[1];

        // check for check, stalemate, and checkmate, sending NotificationMessages accordingly to all players/observers
        checkColorForDanger(gameID, whiteUsername, WHITE, game);
        checkColorForDanger(gameID, blackUsername, BLACK, game);
    }

    private void checkColorForDanger(int gameID, String username, ChessGame.TeamColor color, ChessGame game) {
        if (username != null && game.isInCheckmate(color)) {
            for (Session s : sessions.get(gameID)) {
                sendMessage(s, new NotificationMessage(username + " is in checkmate. Game is over."));
            }
        } else if (username != null && game.isInStalemate(color)) {
            for (Session s : sessions.get(gameID)) {
                sendMessage(s, new NotificationMessage("Stalemate. Game is over."));
            }
        } else {
            if (username != null && game.isInCheck(color)) {
                for (Session s : sessions.get(gameID)) {
                    sendMessage(s, new NotificationMessage(username + " is in check."));
                }
            }
        }
    }

    private void connect(Session session, String username, ConnectCommand command) {
        Collection<GameData> games = gameService.listGames(command.getAuthToken()).games();
        ChessGame game = null;
        for (GameData data : games) {
            if (data.gameID() == command.getGameID()) {
                game = data.game();
            }
        }
        if (game == null) {
            sendMessage(session, new ErrorMessage("Error: could not find game."));
        } else {
            sendMessage(session, new LoadGameMessage(serializer.toJson(game)));
        }
        String notificationAppendage = " as an observer.";
        if (command.getColor() != null && command.getColor() == WHITE) {
            notificationAppendage = " as white.";
        } else if (command.getColor() != null && command.getColor() == BLACK) {
            notificationAppendage = " as black.";
        }
        for (Session s : sessions.get(command.getGameID())) {
            if (s != session) {
                sendMessage(s, new NotificationMessage(username + " has joined the game" + notificationAppendage)); // TODO: as b/w/observer
            }
        }
    }

    private void leaveGame(Session session, String username, LeaveCommand command) {
        try {
            gameService.removePlayerFromGame(username, command.getGameID(), command.getAuthToken());
            removeSession(command.getGameID(), session);
            for (Session s : sessions.get(command.getGameID())) {
                sendMessage(s, new NotificationMessage(username + " has left the game."));
            }
        } catch (Exception ex) {
            sendMessage(session, new ErrorMessage(ex.getMessage()));
        }
    }

    private void resign(Session session, String username, ResignCommand command) {
        try {
            if (authDao.containsToken(command.getAuthToken())) {
                for (Session s : sessions.get(command.getGameID())) {
                    sendMessage(s, new NotificationMessage(username + " has resigned. Game is over."));
                }
            }
        } catch (DataAccessException ex) {
            sendMessage(session, new ErrorMessage("Unexpected error. Please try again."));
        }
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
