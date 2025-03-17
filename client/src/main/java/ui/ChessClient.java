package ui;

import client.ServerFacade;

public class ChessClient {
    private final ServerFacade serverFacade;
    private final String serverUrl;

    public ChessClient(String serverUrl) {
        this.serverFacade = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
    }

    public String help() {
        // TODO: implement
        // returns a list of help options based on the state of the user (logged in or out)
    }

    public String eval(String line) {
        // TODO: implement
        // evaluates a command, using ServerFacade to make http requests, and returns the string that will be printed
    }
}
