package ui.view;

import model.AuthData;
import ui.presenter.LoggedOutPresenter;
import ui.presenter.Presenter;

public class LoggedOutView extends View {
    @Override
    protected void printPrompt() {
        System.out.print("[LOGGED OUT]>>> ");
    }

    @Override
    protected Presenter presenterFactory() {
        return new LoggedOutPresenter(this);
    }

    public void navigateToPregame(AuthData authData) {
        setRunning(false);
        new PregameView(authData).run();
    }
}
