package client;

import ui.LoggedOutView;
import ui.View;

public class ClientMain {
    public static void main(String[] args) {
        View view = new LoggedOutView();
        System.out.println("♕ Oh hey there! Welcome to the 240 Chess Client. Type 'help' or a command to get started.");
        view.run();
    }
}
