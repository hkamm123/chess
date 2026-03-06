package ui.model;

import model.request.RegisterRequest;
import model.result.LoginResult;

public class ServerFacade {
    private final String serverUrl = "http://localhost:8080";
    private final HTTPCommunicator httpCommunicator = new HTTPCommunicator(serverUrl);

    public LoginResult register(RegisterRequest req) throws HttpResponseException {
        return httpCommunicator.sendRequest(LoginResult.class, "POST", "/user", req);
    }
}
