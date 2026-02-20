package ui;

public class PregameView extends View {
    private String username;

    public PregameView(String username) {
        super();
        this.username = username;
    }

    @Override
    protected void printPrompt() {
        System.out.print("[LOGGED IN as " + username + "]>>> ");
    }

    @Override
    protected void eval(String input) {
    }

    @Override
    protected void printHelpString() {
    }
}
