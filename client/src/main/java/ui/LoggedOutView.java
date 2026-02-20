package ui;

public class LoggedOutView extends View {
    @Override
    protected void printPrompt() {
        System.out.print("[LOGGED OUT]>>> ");
    }

    @Override
    protected void eval(String input) {
        System.out.println("You entered " + input); // TODO: change
    }
}
