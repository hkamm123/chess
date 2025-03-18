package client;

import com.google.gson.Gson;
import server.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

public class ServerFacade {
    private final ClientCommunicator clientCommunicator;

    public ServerFacade(String serverUrl) {
        this.clientCommunicator = new ClientCommunicator(serverUrl);
    }

    public RegisterResult register(RegisterRequest req) {
        // TODO: implement
    }

    public LogoutResult clear() { // LogoutResult is reused for this because ClearResult would be same
        // TODO: implement
    }

    public RegisterResult login(LoginRequest req) {
        // TODO: implement
    }

    public LogoutResult logout() {
        // TODO: implement
    }

    public ListResult listGames() {
        // TODO: implement
    }

    public CreateResult createGame() {
        // TODO: implement
    }

    public JoinResult joinGame() {
        // TODO: implement
    }
}
