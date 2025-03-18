package client;

import server.*;

public class ServerFacade {
    private final ClientCommunicator clientCommunicator;

    public ServerFacade(String serverUrl) {
        this.clientCommunicator = new ClientCommunicator(serverUrl);
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
        // TODO: implement
        throw new RuntimeException("Not implemented");
    }

    public RegisterResult login(LoginRequest req) {
        // TODO: implement
        throw new RuntimeException("Not implemented");
    }

    public LogoutResult logout(String authToken) {
        // TODO: implement
        throw new RuntimeException("Not implemented");
    }

    public ListResult listGames(String authToken) {
        // TODO: implement
        throw new RuntimeException("Not implemented");
    }

    public CreateResult createGame(CreateRequest req) {
        // TODO: implement
        throw new RuntimeException("Not implemented");
    }

    public JoinResult joinGame(JoinRequest req) {
        // TODO: implement
        throw new RuntimeException("Not implemented");
    }
}
