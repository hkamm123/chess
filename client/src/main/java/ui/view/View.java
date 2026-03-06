package ui.view;

import ui.presenter.Presenter;

import java.util.Scanner;

public abstract class View {
    private boolean running = true;

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    protected Presenter presenter = presenterFactory();

    public void run() {
        printPrompt();
        String nextInput = readInput();
        while (true) {
            presenter.eval(nextInput);
            if (!running) {
                break;
            }
            printPrompt();
            nextInput = readInput();
        }
    }

    private String readInput() {
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();
    }

    public void displayMessage(String message) {
        System.out.println(message);
    }

    protected abstract void printPrompt();

    protected abstract Presenter presenterFactory();
}
