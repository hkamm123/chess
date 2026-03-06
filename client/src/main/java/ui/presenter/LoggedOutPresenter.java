package ui.presenter;

import model.request.LoginRequest;
import model.request.RegisterRequest;
import model.result.LoginResult;
import ui.model.HttpResponseException;
import ui.model.ServerFacade;
import model.AuthData;
import ui.view.LoggedOutView;

public class LoggedOutPresenter extends Presenter {
    private final LoggedOutView view;
    private final ServerFacade serverFacade = new ServerFacade();

    public LoggedOutPresenter(LoggedOutView view) {
        this.view = view;
    }

    public void eval(String input) {
        String[] args = input.split(" ");
        String helpString = """
                (r)egister <username> <password> <email> - register
                (l)ogin <username> <password> - login
                (h)elp - print the help menu
                (q)uit - quit the program
                """;
        switch (args[0]) {
            case "register", "r" -> register(args);
            case "login", "l" -> login(args);
            case "help", "h" -> view.displayMessage(helpString);
            case "quit", "q" -> view.setRunning(false);
            default -> view.displayMessage("Ope! It looks like that command was not recognized.");
        }
    }

    private void register(String[] args) {
        if (args.length != 4) {
            view.displayMessage("input did not match expected format: (r)egister <username> <password> <email>");
            return;
        }

        try {
            LoginResult result = serverFacade.register(new RegisterRequest(args[1], args[2], args[3]));
            AuthData authData = new AuthData(result.authToken(), result.username());
            view.displayMessage("Registered and logged in successfully!");
            view.navigateToPregame(authData);
        } catch (HttpResponseException ex) {
            view.displayMessage(getErrorMessage(ex));
        }
    }

    private void login(String[] args) {
        if (args.length != 3) {
            view.displayMessage("input did not match expected format: (l)ogin <username> <password>");
            return;
        }

        try {
            LoginResult result = serverFacade.login(new LoginRequest(args[1], args[2]));
            AuthData authData = new AuthData(result.authToken(), result.username());
            view.displayMessage("Logged in successfully!");
            view.navigateToPregame(authData);
        } catch (HttpResponseException ex) {
            view.displayMessage(getErrorMessage(ex));
        }
    }
}
