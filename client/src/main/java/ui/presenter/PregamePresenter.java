package ui.presenter;

import chess.ChessBoard;
import chess.ChessGame;
import model.GameData;
import model.request.CreateRequest;
import model.request.JoinRequest;
import model.result.CreateResult;
import model.result.ListGamesResult;
import ui.model.HttpResponseException;
import ui.model.ServerFacade;
import ui.view.PregameView;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PregamePresenter extends Presenter {
    private final ServerFacade serverFacade = new ServerFacade();
    private final PregameView view;
    private Map<Integer, GameData> gamesList = new HashMap<>();

    public PregamePresenter(PregameView view) {
        this.view = view;
    }

    @Override
    public void eval(String input) {
        String[] args = input.split(" ");
        String helpString = """
                (c)reate <game name> - create a new game
                (g)ames - list all the games
                (p)lay <game number> <color> - play a game
                (o)bserve <game number> - observe a game
                (l)ogout - logout
                (q)uit - logout and quit the program
                """;
        switch (args[0]) {
            case "create", "c" -> createGame(args);
            case "games", "g" -> listGames();
            case "play", "p" -> joinGame(args);
            case "logout", "l" -> logout();
            case "help", "h" -> view.displayMessage(helpString);
            case "quit", "q" -> quit();
        }
    }

    private void createGame(String[] args) {
        if (args.length != 2) {
            view.displayMessage("input did not match expected format: (c)reate <game name>");
            return;
        }

        try {
            CreateResult result = serverFacade.createGame(new CreateRequest(args[1]), view.getAuthToken());
            view.displayMessage("Game created successfully!");
            listGames();
        } catch (HttpResponseException ex) {
            view.displayMessage(getErrorMessage(ex));
        }
    }

    private void listGames() {
        try {
            ListGamesResult result = serverFacade.listGames(view.getAuthToken());
            updateGamesList(result.games());
            displayGamesList();
        } catch (HttpResponseException ex) {
            view.displayMessage(getErrorMessage(ex));
        }
    }

    private void updateGamesList(List<GameData> games) {
        gamesList.clear();
        for (int i = 1; i <= games.size(); i++) {
            gamesList.put(i, games.get(i - 1));
        }
    }

    private void displayGamesList() {
        if (gamesList.size() == 0) {
            view.displayMessage("There are no games yet.");
            return;
        }

        for (int i = 1; i <= gamesList.size(); i++) {
            GameData game = gamesList.get(i);
            String gameName = game.gameName();
            String whiteUsername = (game.whiteUsername() != null ? game.whiteUsername() : "empty");
            String blackUsername = (game.blackUsername() != null ? game.blackUsername() : "empty");
            view.displayMessage(i + ": " + gameName);
            view.displayMessage("\tWhite: " + whiteUsername + "\n\tBlack: " + blackUsername);
        }
    }

    private void joinGame(String[] args) {
        if (args.length != 3) {
            view.displayMessage("input did not match expected format: (p)lay <game number> <color>");
            return;
        }

        try {
            serverFacade.joinGame(new JoinRequest(parseTeamColor(args[2]), gamesList.get(parseGameNumber(args[1])).gameID()), view.getAuthToken());
            view.printBoard(new ChessGame().getBoard(), parseTeamColor(args[2]));
        } catch (IllegalArgumentException ex) {
            view.displayMessage("Ope! Double check your input and try again.");
        } catch (HttpResponseException ex) {
            view.displayMessage(getErrorMessage(ex));
        }
    }

    private ChessGame.TeamColor parseTeamColor(String input) {
        if (input.toLowerCase().equals("white") || input.toLowerCase().equals("w")) {
            return ChessGame.TeamColor.WHITE;
        }

        if (input.toLowerCase().equals("black") || input.toLowerCase().equals("b")) {
            return ChessGame.TeamColor.BLACK;
        }

        throw new IllegalArgumentException("team color input was invalid");
    }

    private int parseGameNumber(String input) {
        if (Integer.parseInt(input) > gamesList.size()) {
            throw new IllegalArgumentException("invalid game number");
        }
        return Integer.parseInt(input);
    }

    private void logout() {
        try {
            serverFacade.logout(view.getAuthToken());
            view.displayMessage("Logged out successfully!");
            view.navigateToLoggedOut();
        } catch (HttpResponseException ex) {
            view.displayMessage(getErrorMessage(ex));
        }
    }

    private void quit() {
        try {
            serverFacade.logout(view.getAuthToken());
            view.setRunning(false);
        } catch (HttpResponseException ex) {
            view.displayMessage(getErrorMessage(ex));
        }
    }
}
