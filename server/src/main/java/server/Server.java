package server;

import com.google.gson.Gson;
import dataaccess.*;
import service.GameService;
import service.UserService;
import spark.*;
import spark.Request;

import static dataaccess.UserDao.BAD_REQUEST_ERR_MSG;
import static dataaccess.UserDao.USER_TAKEN_ERR_MSG;
import static service.UserService.UNAUTHORIZED_ERR_MSG;

public class Server {
    private final Gson serializer = new Gson();

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

        //This line initializes the server and can be removed once you have a functioning endpoint 
//        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    // change these to sql daos when ready
    private final UserDao userDao = new MemoryUserDao();
    private final AuthDao authDao = new MemoryAuthDao();
    private final GameDao gameDao = new MemoryGameDao();

    private final UserService userService = new UserService(userDao, authDao);
    private final GameService gameService = new GameService(gameDao, authDao);

    private Object clear(Request request, Response response) {
        try {
            userService.clear();
        } catch (DataAccessException ex) {
            response.status(500);
            response.body(serialize(new LogoutResult(ex.getMessage())));
        }
        gameService.clear();
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

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
