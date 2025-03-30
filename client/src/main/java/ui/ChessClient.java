package ui;

import chess.ChessGame;
import chess.ChessPosition;
import client.ServerFacade;
import model.GameData;
import server.*;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static chess.ChessGame.TeamColor.BLACK;
import static chess.ChessGame.TeamColor.WHITE;

public class ChessClient implements ServerMessageObserver {
    private final ServerFacade serverFacade;
    private final String serverUrl;
    private List<GameData> games;

    private enum State {
        LOGGEDIN,
        LOGGEDOUT,
        INGAME,
        OBSERVING
    }

    private State state;
    private String authToken;
    private String username;
    private ChessGame currentGame;
    private ChessBoardPrinter.Perspective currentPerspective;

    public ChessClient(String serverUrl) {
        this.serverFacade = new ServerFacade(serverUrl, this);
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

        String inGameMenu = """
                'h' - display a list of commands
                'b' - redraw chess board
                'l' - leave game
                'm' - make move
                'rs' - resign
                'hm' - highlight legal moves
                """;

        String observingMenu = """
                'h' - display a list of commands
                'b' - redraw chess board
                'l' - leave game
                'hm' - highlight legal moves
                """;

        return switch(state) {
            case LOGGEDIN -> postLoginMenu;
            case LOGGEDOUT -> preLoginMenu;
            case INGAME -> inGameMenu;
            case OBSERVING -> observingMenu;
        };
    }

    public String eval(String line) {
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
            case "b" -> redrawBoard();
            case "hm" -> highlightMoves();
            default -> "Ope! That command is not recognized. Please enter 'h' for a list of possible commands.";
        };
    }

    @Override
    public void notify(ServerMessage message) {
        switch (message.getServerMessageType()) {
            case NOTIFICATION -> displayNotification(((NotificationMessage) message).getMessage());
            case ERROR -> displayError(((ErrorMessage) message).getErrorMessage());
            case LOAD_GAME -> loadGame(((LoadGameMessage) message).getGame());
        }
    }

    private void displayNotification(String msg) {
//        TODO: implement
//        this should just print out the notification message to the console
    }

    private void displayError(String msg) {
//        TODO: implement
//        this should print the error message to the console
    }

    private void loadGame(String gameJson) {
//        TODO: implement
//        this should update the currentGame and print the board
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
        this.username = username;
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
        this.username = null;
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
            String gameString = "Game " + (i + 1) + ": " + game.gameName() + "\n";
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
        currentGame = games.get(gameIndex).game();
        currentPerspective = perspective;
        state = State.INGAME;
        ChessBoardPrinter.drawBoard(currentGame, currentPerspective, null);
        return help();
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
        currentPerspective = ChessBoardPrinter.Perspective.WHITE;
        currentGame = games.get(gameIndex).game();
        state = State.OBSERVING;
        ChessBoardPrinter.drawBoard(currentGame, currentPerspective, null);
        return help();
    }

    private String redrawBoard() {
        if (state != State.INGAME && state != State.OBSERVING) {
            return "Looks like you're not currently in a game.";
        }
        if (currentGame == null || currentPerspective == null) { // these are set when the user joins or observes
            return "Ope! Looks like there was an error. Please try again.";
        }
        ChessBoardPrinter.drawBoard(currentGame, currentPerspective, null);
        return "";
    }

    private String highlightMoves() {
        if (state != State.INGAME && state != State.OBSERVING) {
            return "Looks like you're not currently in a game.";
        }
        String input = getInput(
                "Please enter the position of the piece whose moves you want to highlight.\n" +
                "(use the form b2): "
        );
        if (!isValidPosition(input)) {
            return "Ope! That input was not recognized. Please try again.";
        }
        try {
            ChessPosition piecePosition = getPositionFromInput(input);
            ChessBoardPrinter.drawBoard(currentGame, currentPerspective, piecePosition);
            return "";
        } catch (Exception ex) {
            return "Ope! Looks like there was a problem. Please try again.";
        }
    }

    private boolean isValidGameNumber(String gameNumber) {
        return (gameNumber.matches("\\d+") && (Integer.parseInt(gameNumber) - 1 < games.size()));
    }

    private String getInput(String prompt) {
        Scanner scanner = new Scanner(System.in);
        System.out.print(prompt);
        return scanner.nextLine();
    }

    private boolean isValidPosition(String input) {
        return input.matches("[a-h][1-8]");
    }

    private ChessPosition getPositionFromInput(String input) {
        char colLetter = input.charAt(0);
        char rowNumber = input.charAt(1);
        int col = switch(colLetter) {
            case 'a' -> 1;
            case 'b' -> 2;
            case 'c' -> 3;
            case 'd' -> 4;
            case 'e' -> 5;
            case 'f' -> 6;
            case 'g' -> 7;
            case 'h' -> 8;
            default -> throw new IllegalStateException("Unexpected value: " + colLetter);
        };

        int row = Integer.parseInt(String.valueOf(rowNumber));

        return new ChessPosition(row, col);
    }
}
