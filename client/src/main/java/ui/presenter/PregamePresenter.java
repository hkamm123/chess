package ui.presenter;

import ui.model.HttpResponseException;
import ui.model.ServerFacade;
import ui.view.PregameView;

public class PregamePresenter extends Presenter {
    private ServerFacade serverFacade = new ServerFacade();
    private PregameView view;

    private String helpString = """
            (c)reate <game name> - create a new game
            (g)ames - list all the games
            (p)lay <game number> <color> - play a game
            (o)bserve <game number> - observe a game
            (l)ogout - logout
            (q)uit - logout and quit the program
            """;

    public PregamePresenter(PregameView view) {
        this.view = view;
    }

    @Override
    public void eval(String input) {
        String[] args = input.split(" ");
        switch (args[0]) {
            case "quit", "q" -> quit();
            case "logout", "l" -> logout();
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
