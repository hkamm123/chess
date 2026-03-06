package ui.presenter;

import model.request.CreateRequest;
import model.result.CreateResult;
import ui.model.HttpResponseException;
import ui.model.ServerFacade;
import ui.view.PregameView;

public class PregamePresenter extends Presenter {
    private final ServerFacade serverFacade = new ServerFacade();
    private final PregameView view;

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
        } catch (HttpResponseException ex) {
            view.displayMessage(getErrorMessage(ex));
        }
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
