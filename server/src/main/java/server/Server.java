package server;

import com.google.gson.Gson;
import dataaccess.*;
import org.eclipse.jetty.server.Authentication;
import service.GameService;
import service.UserService;
import spark.*;
import spark.Request;

import static dataaccess.UserDao.BAD_REQUEST_ERR_MSG;
import static dataaccess.UserDao.USER_TAKEN_ERR_MSG;
import static service.UserService.UNAUTHORIZED_ERR_MSG;

public class Server {
    private Gson serializer = new Gson();

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

        //This line initializes the server and can be removed once you have a functioning endpoint 
//        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    private UserDao userDao = new MemoryUserDao();
    private AuthDao authDao = new MemoryAuthDao();
    private GameDao gameDao = new MemoryGameDao();
    private UserService userService = new UserService(userDao, authDao);
    private GameService gameService = new GameService(gameDao, authDao);

    private Object clear(Request request, Response response) {
        userService.clear();
        gameService.clear();
        response.status(200);
        response.body("{}");
        return response.body();
    }

    private int setStatus(String message) {
        return switch (message) {
            case null -> 200;
            case USER_TAKEN_ERR_MSG -> 403;
            case BAD_REQUEST_ERR_MSG -> 400;
            case UNAUTHORIZED_ERR_MSG -> 401;
            default -> 500;
        };
    }

    private Object register(Request request, Response response) throws DataAccessException {
        RegisterRequest regReq = (RegisterRequest) serializer.fromJson(request.body(), RegisterRequest.class);
        RegisterResult result = userService.register(regReq);
        response.status(setStatus(result.message()));
        response.body(serialize(result));
        return response.body();
    }

    private Object login(Request request, Response response) {
        LoginRequest loginReq = (LoginRequest) serializer.fromJson(request.body(), LoginRequest.class);
        RegisterResult result = userService.login(loginReq);
        response.status(setStatus(result.message()));
        response.body(serialize(result));
        return response.body();
    }

    private Object logout(Request request, Response response) {
        AuthRequest logoutReq = new AuthRequest(request.headers("Authorization"));
        LogoutResult result = userService.logout(logoutReq);
        response.status(setStatus(result.message()));
        response.body(serialize(result));
        return response.body();
    }

    private Object listGames(Request request, Response response) {
        AuthRequest listReq = new AuthRequest(request.headers("Authorization"));
        ListResult result = gameService.listGames(listReq);
        response.status(setStatus(result.message()));
        response.body(serialize(result));
        return response.body();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
