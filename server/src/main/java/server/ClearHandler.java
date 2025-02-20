package server;

import service.ClearService;
import spark.Request;
import spark.Response;
import spark.Route;

public class ClearHandler extends Handler implements Route {
    private ClearService service = new ClearService();
    @Override
    public server.Request parse(String json) {
        return null;
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        ClearResult result = service.clear();
        if (result.message() == null) {
            response.status(200);
        }
        response.body(serialize(result));
        return response.body();
    }
}
