package ui;

public class LoggedOutView extends View {
    @Override
    protected void printPrompt() {
        System.out.print("[LOGGED OUT]>>> ");
    }

    @Override
    protected void eval(String input) {
        String response = switch(input) {
            default -> "Ope! That command was not recognized.";
        };
        System.out.println(response);
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
