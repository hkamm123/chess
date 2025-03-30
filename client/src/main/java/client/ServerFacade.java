package client;

import server.*;
import ui.WebsocketCommunicator;

public class ServerFacade {
    private final ClientCommunicator clientCommunicator;
    private final WebsocketCommunicator websocketCommunicator;

    public ServerFacade(String serverUrl, ServerMessageObserver smo) {
        this.clientCommunicator = new ClientCommunicator(serverUrl);
        this.websocketCommunicator = new WebsocketCommunicator(serverUrl, smo);
    }

    public RegisterResult register(RegisterRequest req) {
        try {
            return clientCommunicator.makeRequest(
                    "POST",
                    "/user",
                    req,
                    null,
                    RegisterResult.class
            );
        } catch (ResponseException ex) {
            return new RegisterResult(null, null, ex.getMessage());
        }
    }

    public LogoutResult clear() { // LogoutResult is reused for this because ClearResult would be same
        try {
            return clientCommunicator.makeRequest(
                    "DELETE",
                    "/db",
                    null,
                    null,
                    LogoutResult.class);
        } catch (ResponseException ex) {
            return new LogoutResult(ex.getMessage());
        }
    }

    public RegisterResult login(LoginRequest req) {
        try {
            return clientCommunicator.makeRequest(
                    "POST",
                    "/session",
                    req,
                    null,
                    RegisterResult.class
            );
        } catch (ResponseException ex) {
            return new RegisterResult(null, null, ex.getMessage());
        }
    }

    public LogoutResult logout(String authToken) {
        try {
            return clientCommunicator.makeRequest("DELETE",
                    "/session",
                    null,
                    authToken,
                    LogoutResult.class
            );
        } catch (ResponseException ex) {
            return new LogoutResult(ex.getMessage());
        }
    }

    public ListResult listGames(String authToken) {
        try {
            return clientCommunicator.makeRequest(
                    "GET",
                    "/game",
                    null,
                    authToken,
                    ListResult.class);
        } catch (ResponseException ex) {
            return new ListResult(null, ex.getMessage());
        }
    }

    public CreateResult createGame(CreateRequest req, String authToken) {
        try {
            return clientCommunicator.makeRequest("POST", "/game", req, authToken, CreateResult.class);
        } catch (ResponseException ex) {
            return new CreateResult(null, ex.getMessage());
        }
    }

    public JoinResult joinGame(JoinRequest req, String authToken) {
        try {
            return clientCommunicator.makeRequest("PUT", "/game", req, authToken, JoinResult.class);
        } catch (ResponseException ex) {
            return new JoinResult(ex.getMessage());
        }
    }
}
