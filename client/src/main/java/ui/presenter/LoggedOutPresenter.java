package ui.presenter;

import ui.PregameView;

public class LoggedOutPresenter extends Presenter {
    public String eval(String input) {
        String[] args = input.split(" ");
        return switch(args[0]) {
            case "register" -> register(args);
            default -> "Ope, sorry! That input was not recognized.";
        };
    }

    private String register(String[] args) {
        if (args.length != 4) {
            return "input did not match expected format: register <username> <password> <email>";
        }

        // TODO: use ServerFacade to make register request to server
        new PregameView(args[1]).run();
        return "";
    }
}
