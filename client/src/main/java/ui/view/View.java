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
            if (nextInput.equals("quit")) {
                cleanup();
                break;
            } else if (nextInput.equals("help") || nextInput.equals("h")) {
                printHelpString();
            } else {
                presenter.eval(nextInput);
                if (!running) {
                    cleanup();
                    break;
                }
            }
            printPrompt();
            nextInput = readInput();
        }
    }

    private String readInput() {
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();
    }

    protected abstract void printPrompt();

    protected abstract void printHelpString();

    protected void cleanup() {
        return;
    }

    protected abstract Presenter presenterFactory();
}
