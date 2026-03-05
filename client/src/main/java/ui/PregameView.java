package ui;

import ui.presenter.PregamePresenter;
import ui.presenter.Presenter;

public class PregameView extends View {
    private final String username;

    public PregameView(String username) {
        super();
        this.username = username;
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
