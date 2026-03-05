package ui;

import ui.presenter.LoggedOutPresenter;
import ui.presenter.Presenter;

public class LoggedOutView extends View {
    @Override
    protected void printPrompt() {
        System.out.print("[LOGGED OUT]>>> ");
    }

    @Override
    protected void printHelpString() {
        System.out.print("""
                register <username> <password> <email> - register
                login <username> <password> - login
                help - print the help menu
                """);
    }

    @Override
    protected Presenter presenterFactory() {
        return new LoggedOutPresenter();
    }
}
