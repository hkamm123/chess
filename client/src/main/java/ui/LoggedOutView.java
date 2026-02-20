package ui;

import ui.presenter.LoggedOutPresenter;

public class LoggedOutView extends View {
    private LoggedOutPresenter presenter = new LoggedOutPresenter();

    @Override
    protected void printPrompt() {
        System.out.print("[LOGGED OUT]>>> ");
    }

    @Override
    protected void eval(String input) {
        System.out.println(presenter.eval(input));
    }

    @Override
    protected void printHelpString() {
        System.out.print("""
                register <username> <password> <email> - register
                login <username> <password> - login
                help - print the help menu
                """);
    }
}
