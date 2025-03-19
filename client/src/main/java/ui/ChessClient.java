package ui;

import chess.ChessBoard;
import chess.ChessGame;
import client.ServerFacade;
import model.GameData;
import server.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static chess.ChessGame.TeamColor.BLACK;
import static chess.ChessGame.TeamColor.WHITE;

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
                
                'o' - observe a game as a non-player
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
            case "j" -> joinGame();
            case "o" -> observeGame();
            default -> "Ope! That command is not recognized. Please enter 'h' for a list of possible commands.";
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

    private String joinGame() {
        if (state != State.LOGGEDIN) {
            return "Please login to join a game.";
        }
        System.out.print(listGames());
        String gameNumber = getInput("Please enter the number of the game you wish to join: ");
        if (!isValidGameNumber(gameNumber)) {
            return "Ope! Looks like you entered an invalid game number.\n" + help();
        }
        int gameIndex = Integer.parseInt(gameNumber) - 1;
        int gameID = games.get(gameIndex).gameID();
        String colorString = getInput("Enter the desired color ('w' for white and 'b' for black): ");
        ChessGame.TeamColor color = null;
        ChessBoardPrinter.Perspective perspective = null;
        if (colorString.equals("w")) {
            if (games.get(gameIndex).whiteUsername() != null) {
                return "Ope! Looks like that spot is already taken.\n" + help();
            }
            color = WHITE;
            perspective = ChessBoardPrinter.Perspective.WHITE;
        } else if (colorString.equals("b")) {
            if (games.get(gameIndex).blackUsername() != null) {
                return "Ope! Looks like that spot is already taken.\n" + help();
            }
            color = BLACK;
            perspective = ChessBoardPrinter.Perspective.BLACK;
        } else {
            return "Ope! Looks like your input was invalid.";
        }
        JoinResult result = serverFacade.joinGame(new JoinRequest(color, gameID), authToken);
        if (result.message() != null) {
            return result.message();
        }
        ChessBoardPrinter.drawBoard(games.get(gameIndex).game().getBoard(), perspective);
        return "";
    }

    private String observeGame() {
        if (state != State.LOGGEDIN) {
            return "Please login to observe a game.";
        }
        System.out.print(listGames());
        String gameNumber = getInput("Please enter the number of the game you wish to observe: ");
        if (!isValidGameNumber(gameNumber)) {
            return "Ope! Looks like you entered an invalid game number.\n" + help();
        }
        int gameIndex = Integer.parseInt(gameNumber) - 1;
        ChessBoardPrinter.drawBoard(games.get(gameIndex).game().getBoard(), ChessBoardPrinter.Perspective.WHITE);
        return "";
    }

    private boolean isValidGameNumber(String gameNumber) {
        return (gameNumber.matches("\\d+") && (Integer.parseInt(gameNumber) - 1 < games.size()));
    }

    private String getInput(String prompt) {
        Scanner scanner = new Scanner(System.in);
        System.out.print(prompt);
        return scanner.nextLine();
    }
}
