package ui.presenter;

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
            case "logout", "l" -> logout();
            case "help", "h" -> view.displayMessage(helpString);
            case "quit", "q" -> quit();
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
