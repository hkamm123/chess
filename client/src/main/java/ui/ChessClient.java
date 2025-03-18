package ui;

import client.ServerFacade;

public class ChessClient {
    private final ServerFacade serverFacade;
    private final String serverUrl;

    private enum State {
        LOGGEDIN,
        LOGGEDOUT
    }
    private State state;

    public ChessClient(String serverUrl) {
        this.serverFacade = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
        this.state = State.LOGGEDOUT;
    }

    public String help() {
        // returns a list of help options based on the state of the user (logged in or out)

        String preLoginMenu = """
                'h' - display list of commands
                
                'r' - register a new user
                
                'li' - login an existing user
                
                'quit' - quit the program
                """;

        String postLoginMenu = """
                'h' - display list of commands
                
                'lo' - logout of the session
                
                'c' - create a new game
                
                'lg' - list games
                
                'j' - join a game as a player
                
                'o' - join a game as an observer
                """;

        if (state == State.LOGGEDIN) {
            return postLoginMenu;
        } else {
            return preLoginMenu;
        }
    }

    public String eval(String line) {
        // TODO: implement
        // evaluates a command, using ServerFacade to make http requests, and returns the string that will be printed
        return switch (line) {
            case "quit" -> "quit";
            case "h" -> help();
            default -> "Oops! That command is not recognized. Please enter 'h' for a list of possible commands.";
        };
    }
}
