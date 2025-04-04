package ui;

import chess.*;
import client.ServerFacade;
import com.google.gson.Gson;
import model.GameData;
import server.*;
import websocket.commands.ConnectCommand;
import websocket.commands.LeaveCommand;
import websocket.commands.MakeMoveCommand;
import websocket.commands.ResignCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static chess.ChessGame.TeamColor.BLACK;
import static chess.ChessGame.TeamColor.WHITE;
import static chess.ChessPiece.PieceType.*;
import static ui.EscapeSequences.*;

public class ChessClient implements ServerMessageObserver {
    private final ServerFacade serverFacade;
    private final String serverUrl;
    private List<GameData> games;
    private Gson serializer;
    private int currentGameID;

    private enum State {
        PREGAME,
        LOGGEDOUT,
        INGAME,
        OBSERVING,
        GAMEOVER
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
        this.serializer = new Gson();
        this.currentGameID = 0;
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
            case PREGAME -> postLoginMenu;
            case LOGGEDOUT -> preLoginMenu;
            case INGAME -> inGameMenu;
            case OBSERVING, GAMEOVER -> observingMenu;
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
            case "l" -> leaveGame();
            case "m" -> makeMove();
            case "rs" -> resign();
            default -> "Ope! That command is not recognized. Please enter 'h' for a list of possible commands.";
        };
    }

    @Override
    public void notify(String msgJson) {
        ServerMessage message = serializer.fromJson(msgJson, ServerMessage.class);
        switch (message.getServerMessageType()) {
            case NOTIFICATION -> displayNotification(((NotificationMessage) serializer.fromJson(msgJson, NotificationMessage.class)).getMessage());
            case ERROR -> displayError(((ErrorMessage) serializer.fromJson(msgJson, ErrorMessage.class)).getErrorMessage());
            case LOAD_GAME -> loadGame((LoadGameMessage) serializer.fromJson(msgJson, LoadGameMessage.class));
        }
    }

    private void displayNotification(String msg) {
        System.out.println(SET_TEXT_COLOR_GREEN + msg + SET_TEXT_COLOR_WHITE);
    }

    private void displayError(String msg) {
        System.out.println(SET_TEXT_COLOR_RED + msg + SET_TEXT_COLOR_WHITE); // TODO: hide these messages from the user
    }

    private void loadGame(LoadGameMessage message) {
        this.currentGame = serializer.fromJson(message.getGame(), ChessGame.class);
        ChessBoardPrinter.drawBoard(currentGame, currentPerspective, null);
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
        state = State.PREGAME;
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
        state = State.PREGAME;
        this.username = username;
        return "Logged in as " + username + "\n" + help();
    }

    private String logout() {
        if (state == State.LOGGEDOUT) {
            return "It looks like you're not logged in.";
        } else if (state == State.INGAME || state == State.OBSERVING) {
            return "Please leave the current game to logout.";
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
        if (state != State.PREGAME) {
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
        if (state != State.PREGAME) {
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
        if (state != State.PREGAME) {
            return "Please login to join a game.";
        }
        System.out.print(listGames());
        String gameNumber = getInput("Please enter the number of the game you wish to join: ");
        if (!isValidGameNumber(gameNumber)) {
            return "Ope! Looks like you entered an invalid game number.\n" + help();
        }
        int gameIndex = Integer.parseInt(gameNumber) - 1;
        int gameID = games.get(gameIndex).gameID();
        currentGameID = gameID;
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
        currentPerspective = perspective;
        JoinResult result = serverFacade.joinGame(new JoinRequest(color, gameID), authToken);
        if (result.message() != null) {
            return result.message();
        }
        state = State.INGAME;
        return "";
    }

    private String observeGame() {
        if (state != State.PREGAME) {
            return "Please login to observe a game.";
        }
        System.out.print(listGames());
        String gameNumber = getInput("Please enter the number of the game you wish to observe: ");
        if (!isValidGameNumber(gameNumber)) {
            return "Ope! Looks like you entered an invalid game number.\n" + help();
        }
        int gameIndex = Integer.parseInt(gameNumber) - 1;
        currentGameID = games.get(gameIndex).gameID();
        currentPerspective = ChessBoardPrinter.Perspective.WHITE;
        try {
            serverFacade.sendCommand(new ConnectCommand(authToken, currentGameID, null));
            state = State.OBSERVING;
        } catch (Exception ex) {
            ex.printStackTrace(); // TODO: take this out
            return "Ope! Looks like there was an error. Please try again.";
        }
//        currentGame = games.get(gameIndex).game();
//        ChessBoardPrinter.drawBoard(currentGame, currentPerspective, null);
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

    private String leaveGame() {
        if (state != State.INGAME && state != State.OBSERVING) {
            return "Looks like you're not in a game.";
        }
        try {
            serverFacade.sendCommand(new LeaveCommand(authToken, currentGameID));
            currentGameID = 0;
            currentGame = null;
            currentPerspective = null;
            state = State.PREGAME;
            return "";
        } catch (Exception ex) {
            ex.printStackTrace(); //TODO: hide this
            return "Ope! Looks like an error has occurred. Please try again.";
        }
    }

    private String makeMove() { // consider breaking the input handling into a separate method
        if (state != State.INGAME) {
            return "Ope! Looks like you're not currently playing a game.";
        }

        // handling inputs
        String startPosInput = getInput("Please enter the position of the piece you would like to move: ");
        String endPosInput = getInput("Please enter the position to which you'd like to move: ");
        if (!isValidPosition(startPosInput) || !isValidPosition(endPosInput)) {
            return "Ope! Looks like your input was invalid.";
        }
        ChessPosition startPos = getPositionFromInput(startPosInput);
        ChessPosition endPos = getPositionFromInput(endPosInput);
        ChessPiece.PieceType promotionPiece = null;
        ChessPiece movingPiece = currentGame.getBoard().getPiece(startPos);
        if (movingPiece.getPieceType() == PAWN) {
            if ((currentPerspective == ChessBoardPrinter.Perspective.WHITE && endPos.getRow() == 8)
                    || currentPerspective == ChessBoardPrinter.Perspective.BLACK && endPos.getRow() == 1) {
                String promoPieceInput = getInput("Enter the type of piece you'd like to promote the pawn to: ");
                try {
                    promotionPiece = getPieceTypeFromInput(promoPieceInput);
                } catch (IllegalStateException ex) {
                    return "Ope! Looks like your input was invalid.";
                }
            }
        }
        ChessMove requestedMove = new ChessMove(startPos, endPos, promotionPiece);

        // checking the move locally
        if (!currentGame.validMoves(startPos).contains(requestedMove)) {
            return SET_TEXT_COLOR_RED + "Ope! Looks like that move is not valid." + SET_TEXT_COLOR_WHITE;
        }

        // updating the board through websocket connection
        MakeMoveCommand cmd = new MakeMoveCommand(authToken, currentGameID, requestedMove);
        try {
            serverFacade.sendCommand(cmd);
        } catch (Exception ex) {
            ex.printStackTrace(); // TODO: remove this and handle it before it gets to user
            return "Ope! Looks like there was an error.";
        }
        return "";
    }

    private String resign() {
        if (state != State.INGAME) {
            return "Ope! Looks like you're not currently playing a game.";
        }
        ResignCommand cmd = new ResignCommand(authToken, currentGameID);
        try {
            serverFacade.sendCommand(cmd);
            state = State.GAMEOVER;
        } catch (Exception ex) {
            ex.printStackTrace(); // TODO: handle this better
            return "Ope! Looks like there was an error.";
        }
        return "";
    }

    private ChessPiece.PieceType getPieceTypeFromInput(String input) {
        return switch (input.toLowerCase()) {
            case "queen" -> QUEEN;
            case "rook" -> ROOK;
            case "bishop" -> BISHOP;
            case "knight" -> KNIGHT;
            default -> throw new IllegalStateException("invalid input");
        };
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
