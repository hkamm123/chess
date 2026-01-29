package server;

import com.google.gson.Gson;
import io.javalin.*;
import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;
import server.request.*;

public class Server {

    private final Javalin javalin;
    private final Gson gson;

    public Server() {
        gson = new Gson();
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.
        javalin.post("/user", this::registrationHandler);
    }

    private void registrationHandler(@NotNull Context context) {
        RegisterRequest request = gson.fromJson(context.body(), RegisterRequest.class);
        // TODO: pass request to Service, send back response
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
