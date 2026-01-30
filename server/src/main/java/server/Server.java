package server;

import com.google.gson.Gson;
import io.javalin.*;
import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;
import server.request.*;
import server.result.LoginResult;
import service.UserService;

public class Server {

    private final Javalin javalin;
    private final Gson gson;
    private final UserService userService;

    public Server() {
        gson = new Gson();
        userService = new UserService();

        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.
        javalin.post("/user", this::registrationHandler);
    }

    private void registrationHandler(@NotNull Context context) {
        RegisterRequest request = gson.fromJson(context.body(), RegisterRequest.class);
        LoginResult result = userService.register(request);
        // TODO: handle errors and send result back
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
