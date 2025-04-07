package client;

import com.google.gson.Gson;
import server.*;
import ui.ServerMessageObserver;
import ui.WebsocketCommunicator;
import websocket.commands.ConnectCommand;
import websocket.commands.UserGameCommand;

public class ServerFacade {
    private final ClientCommunicator clientCommunicator;
    private final WebsocketCommunicator websocketCommunicator;
    private Gson serializer;

    public ServerFacade(String serverUrl, ServerMessageObserver smo) {
        this.clientCommunicator = new ClientCommunicator(serverUrl);
        this.websocketCommunicator = new WebsocketCommunicator(serverUrl, smo);
        this.serializer = new Gson();
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
            ConnectCommand command = new ConnectCommand(authToken, req.gameID(), req.playerColor());
            sendCommand(command);
            return clientCommunicator.makeRequest("PUT", "/game", req, authToken, JoinResult.class);
        } catch (Exception ex) {
            return new JoinResult(ex.getMessage());
        }
    }

    public void sendCommand(UserGameCommand command) throws Exception {
        String commandJson = serializer.toJson(command);
        websocketCommunicator.send(commandJson);
    }
}
