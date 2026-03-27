package ui.model;

import model.request.CreateRequest;
import model.request.JoinRequest;
import model.request.LoginRequest;
import model.request.RegisterRequest;
import model.result.CreateResult;
import model.result.ListGamesResult;
import model.result.LoginResult;

public class ServerFacade {
    private final String serverUrl = "http://localhost:8080";
    private final HTTPCommunicator httpCommunicator = new HTTPCommunicator(serverUrl);

    public LoginResult register(RegisterRequest req) throws HttpResponseException {
        return httpCommunicator.sendRequest(LoginResult.class, "POST", "/user", req);
    }

    public LoginResult login(LoginRequest req) throws HttpResponseException {
        return httpCommunicator.sendRequest(LoginResult.class, "POST", "/session", req);
    }

    public void logout(String authToken) throws HttpResponseException {
        httpCommunicator.sendRequest("DELETE", "/session", authToken);
    }

    public CreateResult createGame(CreateRequest req, String authToken) throws HttpResponseException {
        return httpCommunicator.sendRequest(CreateResult.class, "POST", "/game", req, authToken);
    }

    public ListGamesResult listGames(String authToken) throws HttpResponseException {
        return httpCommunicator.sendRequest(ListGamesResult.class, "GET", "/game", authToken);
    }

    public void observeGame(int gameID) throws HttpResponseException {
        // TODO: in phase 6, implement websocket
        return;
    }

    public void joinGame(JoinRequest req, String authToken) throws HttpResponseException {
        httpCommunicator.sendRequest("PUT", "/game", req, authToken);
    }
}
