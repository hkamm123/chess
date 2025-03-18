package ui;

import client.ServerFacade;
import model.GameData;
import server.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;

public class ChessClient {
    private final ServerFacade serverFacade;
    private final String serverUrl;
    private List<GameData> games;

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
        this.games = new ArrayList<GameData>();
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
            case "c" -> createGame();
            case "lg" -> listGames();
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

    private String createGame() {
        if (state != State.LOGGEDIN) {
            return "Please login to create a game.";
        }
        String gameName = getInput("Please enter a game name: ");
        CreateResult result = serverFacade.createGame(new CreateRequest(gameName), authToken);
        if (result.message() != null) {
            return result.message();
        }
        return "Game created successfully!\n" + listGames();
    }

    private String listGames() {
        if (state != State.LOGGEDIN) {
            return "Please login to see games.";
        }
        ListResult result = serverFacade.listGames(authToken);
        if (result.message() != null) {
            return result.message();
        }
        games = new ArrayList<>();
        games.addAll(result.games());
        return displayGamesAsString();
    }

    private String displayGamesAsString() {
        StringBuilder gamesStringBuilder = new StringBuilder();
        for (int i = 0; i < games.size(); i++) {
            GameData game = games.get(i);
            String gameString = "Game " + (i+1) + ": " + game.gameName() + "\n";
            gamesStringBuilder.append(gameString);
            String blackPlayer = "open";
            String whitePlayer = "open";
            if (game.whiteUsername() != null) {
                whitePlayer = game.whiteUsername();
            }
            if (game.blackUsername() != null) {
                blackPlayer = game.blackUsername();
            }
            gamesStringBuilder.append("  White Player: " + whitePlayer + "\n");
            gamesStringBuilder.append("  Black Player: " + blackPlayer + "\n");
        }
        return gamesStringBuilder.toString();
    }

    private String getInput(String prompt) {
        Scanner scanner = new Scanner(System.in);
        System.out.print(prompt);
        return scanner.nextLine();
    }
}
