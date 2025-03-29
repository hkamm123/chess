package server;

import chess.ChessMove;
import com.google.gson.Gson;
import dataaccess.*;
import model.AuthData;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import service.GameService;
import service.UserService;
import spark.*;
import spark.Request;

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

    public Server() {
        try {
            userDao = new MySQLUserDao();
            authDao = new MySQLAuthDao();
            gameDao = new MySQLGameDao();
            userService = new UserService(userDao, authDao);
            gameService = new GameService(gameDao, authDao);
        } catch (DataAccessException ex) {
            throw new RuntimeException("Error initializing server: " + ex.getMessage());
        }
    }

    private String serialize(Object result) {
        return serializer.toJson(result);
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.post("/user", this::register);
        Spark.delete("/db", this::clear);
        Spark.post("/session", this::login);
        Spark.delete("/session", this::logout);
        Spark.get("/game", this::listGames);
        Spark.post("/game", this::createGame);
        Spark.put("/game", this::joinGame);

        Spark.webSocket("/ws", Server.class);

        //This line initializes the server and can be removed once you have a functioning endpoint
//        Spark.init();

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

    private void makeMove(String authToken, int gameID, ChessMove move) {
        //TODO: implement
        // this very well could need a different return type or different parameters
        // the purpose of this is not a new endpoint, but just updating the game in the database
        // this will require corresponding methods in GameService and GameDAO
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws Exception {
        //TODO: implement
        // 1. deserialize message into appropriate subclass of UserGameCommand (or double deserialize)
        // 1.5 authorize user by checking authtoken
        // 2. make necessary changes to db and/or session
        // 3. create appropriate ServerMessage
        // 4. serialize and send the message for the WebsocketCommunicator to handle
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
