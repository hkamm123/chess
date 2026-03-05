package ui.presenter;

import model.AuthData;
import ui.view.LoggedOutView;

public class LoggedOutPresenter extends Presenter {
    private LoggedOutView view;

    public LoggedOutPresenter(LoggedOutView view) {
        this.view = view;
    }

    public void eval(String input) {
        String[] args = input.split(" ");
        switch (args[0]) {
            case "register" -> register(args);
            case "login" -> login(args);
            default -> view.displayMessage("Ope! It looks like that command was not recognized.");
        }
    }

    private void register(String[] args) {
        if (args.length != 4) {
            view.displayMessage("input did not match expected format: register <username> <password> <email>");
            return;
        }

        // TODO: use ServerFacade to make register request to server
        AuthData authData = new AuthData("some token", args[1]);
        view.displayMessage("Registered and logged in successfully!");
        view.navigateToPregame(authData);
    }

    private void login(String[] args) {
        if (args.length != 3) {
            view.displayMessage("input did not match expected format: login <username> <password>");
            return;
        }

        // TODO: serverFacade request
        AuthData authData = new AuthData("some token", args[1]);
        view.displayMessage("Logged in successfully!");
        view.navigateToPregame(authData);
    }
}
