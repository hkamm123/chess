package server;

import dataaccess.UserDao;
import service.UserService;
import spark.Response;
import spark.Route;

import java.util.Objects;

public class RegisterHandler extends Handler implements Route {
    private UserService service = new UserService();
    @Override
    public Request parse(String json) {
        return (RegisterRequest) serializer.fromJson(json, RegisterRequest.class);
    }

    //    TODO: add a handle() method which will parse the json input and send it to a service, and return the json for a RegisterResult object

    @Override
    public Object handle(spark.Request request, Response response) throws Exception {
        RegisterRequest regReq = (RegisterRequest) parse(request.body());
        RegisterResult result = service.register(regReq);
        if (result.message() == null) {
            response.status(200);
            response.body(serialize(result));
        }
        return response.body();
    }
}
