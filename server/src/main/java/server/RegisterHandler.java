package server;

import dataaccess.UserDao;
import service.UserService;
import spark.Response;
import spark.Route;

import java.util.Objects;

import static dataaccess.UserDao.BAD_REQUEST_ERR_MSG;
import static dataaccess.UserDao.USER_TAKEN_ERR_MSG;

public class RegisterHandler extends Handler implements Route {
    private UserService service = new UserService();
    @Override
    public Request parse(String json) {
        return (RegisterRequest) serializer.fromJson(json, RegisterRequest.class);
    }

    @Override
    public Object handle(spark.Request request, Response response) throws Exception {
        RegisterRequest regReq = (RegisterRequest) parse(request.body());
        RegisterResult result = service.register(regReq);
        if (result.message() == null) {
            response.status(200);
        } else if (result.message().equals(USER_TAKEN_ERR_MSG)) {
            response.status(403);
        } else if (result.message().equals(BAD_REQUEST_ERR_MSG)) {
            response.status(400);
        } else {
            response.status(500);
        }
        response.body(serialize(result));
        return response.body();
    }
}
