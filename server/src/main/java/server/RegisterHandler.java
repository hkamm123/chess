package server;

import spark.Response;
import spark.Route;

public class RegisterHandler extends Handler implements Route {
    @Override
    public Request parse(String json) {
        return (RegisterRequest) serializer.fromJson(json, RegisterRequest.class);
    }

    //    TODO: add a handle() method which will parse the json input and send it to a service, and return the json for a RegisterResult object

    @Override
    public Object handle(spark.Request request, Response response) throws Exception {
        return "Received Request. Body: " + request.body();
    }
}
