package ui;

import client.ServerFacade;
import server.LoginRequest;
import server.LogoutResult;
import server.RegisterRequest;
import server.RegisterResult;

import java.util.Scanner;

public class ChessClient {
    private final ServerFacade serverFacade;
    private final String serverUrl;

    private enum State {
        LOGGEDIN,
        LOGGEDOUT
    }
    private State state;
    private String authToken;

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
            case "r" -> register();
            case "li" -> login();
            case "lo" -> logout();
            default -> "Oops! That command is not recognized. Please enter 'h' for a list of possible commands.";
        };
    }

    private String register() {
        String username = getInput("Please enter the username you wish to register: ");
        String password = getInput("Please enter the password you wish to register: ");
        String email = getInput("Please enter the email you wish to register: ");
        RegisterResult result = serverFacade.register(new RegisterRequest(username, password, email));
        if (result.message() != null) {
            return result.message();
        }
        authToken = result.authToken();
        state = State.LOGGEDIN;
        return "Registration Successful! Logged in as " + username + "\n" + help();
    }

    private String login() {
        String username = getInput("Please enter your username: ");
        String password = getInput("Please enter your password: ");
        RegisterResult result = serverFacade.login(new LoginRequest(username, password));
        if (result.message() != null) {
            return result.message();
        }
        authToken = result.authToken();
        state = State.LOGGEDIN;
        return "Logged in as " + username + "\n" + help();
    }

    private String logout() {
        if (state != State.LOGGEDIN) {
            return "It looks like you're not logged in.";
        }
        LogoutResult result = serverFacade.logout(authToken);
        if (result.message() != null) {
            return result.message();
        }
        state = State.LOGGEDOUT;
        authToken = "";
        return "Logged out successfully!" + "\n" + help();
    }

    private String getInput(String prompt) {
        Scanner scanner = new Scanner(System.in);
        System.out.print(prompt);
        return scanner.nextLine();
    }
}
