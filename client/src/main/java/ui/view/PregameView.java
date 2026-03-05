package ui.view;

import model.AuthData;
import ui.presenter.PregamePresenter;
import ui.presenter.Presenter;

public class PregameView extends View {
    private final String username;
    private final String authToken;

    public PregameView(AuthData authData) {
        super();
        this.username = authData.username();
        this.authToken = authData.authToken();
    }

    @Override
    protected void printPrompt() {
        System.out.print("[LOGGED IN as " + username + "]>>> ");
    }

    @Override
    protected void printHelpString() {
    }

    @Override
    protected Presenter presenterFactory() {
        return new PregamePresenter();
    }
}
