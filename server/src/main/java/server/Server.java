package server;

import com.google.gson.Gson;
import dataaccess.AuthDao;
import dataaccess.MemoryAuthDao;
import dataaccess.MemoryUserDao;
import dataaccess.UserDao;
import io.javalin.*;
import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;
import server.request.*;
import server.result.LoginResult;
import service.ServiceException;
import service.UserService;

import java.util.Map;

public class Server {

    private final Javalin javalin;
    private final Gson gson;
    private final UserService userService;

    public Server() {
        gson = new Gson();
        UserDao userDao = new MemoryUserDao();
        AuthDao authDao = new MemoryAuthDao();
        userService = new UserService(userDao, authDao);

        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.
        javalin.post("/user", this::registrationHandler);

        javalin.exception(ServiceException.class, this::exceptionHandler);
    }

    private void registrationHandler(@NotNull Context context) throws ServiceException {
        RegisterRequest request = gson.fromJson(context.body(), RegisterRequest.class);
        LoginResult result = userService.register(request);
        context.status(200);
        context.json(gson.toJson(result));
    }

    private void exceptionHandler(@NotNull ServiceException e, @NotNull Context context) {
        switch (e.getType()) {
            case ALREADY_TAKEN -> {
                context.status(403);
                context.json(gson.toJson(Map.of("message", "Error: already taken")));
            }
            case SERVER_ERROR -> {
                context.status(500);
                context.json(gson.toJson(Map.of("message", "Error: unknown server error")));
            }
            case BAD_REQUEST -> {
                context.status(400);
                context.json(gson.toJson(Map.of("message", "Error: bad request")));
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
