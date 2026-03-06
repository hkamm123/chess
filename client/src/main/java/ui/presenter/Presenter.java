package ui.presenter;

import ui.model.HttpResponseException;

public abstract class Presenter {
    public abstract void eval(String input);

    protected String getErrorMessage(HttpResponseException ex) {
        return switch (ex.getStatusCode()) {
            case 400 -> "Ope! It looks like an error occurred. Please check your input and try again.";
            case 401 -> "Ope! You're not authorized there, bud!";
            case 403 -> "Ope, sorry bud! That's already taken.";
            case 500 -> "Ope, sorry! There was an error in the server. Go ahead and try again.";
            default -> throw new IllegalStateException("Unexpected status code value: " + ex.getStatusCode());
        };
    }
}
