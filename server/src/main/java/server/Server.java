package server;

import com.google.gson.Gson;
import dataaccess.*;
import io.javalin.*;
import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;
import server.request.*;
import server.result.ListGamesResult;
import server.result.LoginResult;
import service.GameService;
import service.ServiceException;
import service.UserService;

import java.util.Map;

public class Server {

    private final Javalin javalin;
    private final Gson gson;
    private final UserService userService;
    private final GameService gameService;

    public Server() {
        gson = new Gson();
        UserDao userDao = new MemoryUserDao();
        AuthDao authDao = new MemoryAuthDao();
        GameDao gameDao = new MemoryGameDao();
        userService = new UserService(userDao, authDao);
        gameService = new GameService(gameDao, authDao);

        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.
        javalin.post("/user", this::registrationHandler);
        javalin.post("/session", this::loginHandler);
        javalin.delete("/session", this::logoutHandler);
        javalin.get("/game", this::getGamesHandler);

        javalin.exception(ServiceException.class, this::exceptionHandler);
    }

    private void registrationHandler(@NotNull Context context) throws ServiceException {
        RegisterRequest request = gson.fromJson(context.body(), RegisterRequest.class);
        if (request.username() == null || request.password() == null || request.email() == null) {
            throw new ServiceException(ServiceException.ServiceExceptionType.BAD_REQUEST);
        }
        LoginResult result = userService.register(request);
        context.status(200);
        context.json(gson.toJson(result));
    }

    private void loginHandler(@NotNull Context context) throws ServiceException{
        LoginRequest request = gson.fromJson(context.body(), LoginRequest.class);
        if (request.username() == null || request.password() == null) {
            throw new ServiceException(ServiceException.ServiceExceptionType.BAD_REQUEST);
        }
        LoginResult result = userService.login(request);
        context.status(200);
        context.json(gson.toJson(result));
    }

    private void logoutHandler(@NotNull Context context) throws ServiceException {
        String authToken = context.header("authorization");
        if (authToken == null || authToken.isEmpty()) {
            throw new ServiceException(ServiceException.ServiceExceptionType.BAD_REQUEST);
        }
        userService.logout(authToken);
        context.status(200);
    }

    private void getGamesHandler(@NotNull Context context) throws ServiceException {
        String authToken = context.header("authorization");
        if (authToken == null || authToken.isEmpty()) {
            throw new ServiceException(ServiceException.ServiceExceptionType.BAD_REQUEST);
        }
        ListGamesResult result = gameService.listGames(authToken);
        context.status(200);
        context.json(gson.toJson(result));
    }

    private void exceptionHandler(@NotNull ServiceException e, @NotNull Context context) {
        switch (e.getType()) {
            case BAD_REQUEST -> {
                context.status(400);
                context.json(gson.toJson(Map.of("message", "Error: bad request")));
            }
            case UNAUTHORIZED -> {
                context.status(401);
                context.json(gson.toJson(Map.of("message", "Error: unauthorized")));
            }
            case ALREADY_TAKEN -> {
                context.status(403);
                context.json(gson.toJson(Map.of("message", "Error: already taken")));
            }
            case SERVER_ERROR -> {
                context.status(500);
                context.json(gson.toJson(Map.of("message", "Error: unknown server error")));
            }
        }
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
