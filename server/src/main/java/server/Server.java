package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import org.eclipse.jetty.server.Authentication;
import service.UserService;
import spark.*;
import spark.Request;

import static dataaccess.UserDao.BAD_REQUEST_ERR_MSG;
import static dataaccess.UserDao.USER_TAKEN_ERR_MSG;

public class Server {
//    TODO: get rid of handler classes and instead implement handlers as functions in this class
//    TODO: get rid of ClearService and instead implement clear() in other services
//    TODO: initialize an instance of each service in this class, which will store their respective dao instances
    private ClearHandler clearHandler = new ClearHandler();

    private Gson serializer = new Gson();
    private String serialize(Object result) {
        return serializer.toJson(result);
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.post("/user", this::register);
        Spark.delete("/db", clearHandler);

        //This line initializes the server and can be removed once you have a functioning endpoint 
//        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    private UserService userService = new UserService();

    private Object register(Request request, Response response) throws DataAccessException {
        RegisterRequest regReq = (RegisterRequest) serializer.fromJson(request.body(), RegisterRequest.class);
        RegisterResult result = userService.register(regReq);
        switch (result.message()) {
            case null: response.status(200);
            break;
            case USER_TAKEN_ERR_MSG: response.status(403);
            break;
            case BAD_REQUEST_ERR_MSG: response.status(400);
            break;
            default: response.status(500);
        }
        response.body(serialize(result));
        return response.body();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
