package ui;

import ui.presenter.Presenter;

import java.util.Scanner;

public abstract class View {
    protected Presenter presenter = presenterFactory();

    public void run() {
        printPrompt();
        String nextInput = readInput();
        while (!nextInput.equals("quit")) {
            if (nextInput.equals("help") || nextInput.equals("h")) {
                printHelpString();
            } else {
                System.out.println(presenter.eval(nextInput));
            }
            printPrompt();
            nextInput = readInput();
        }
        System.out.println("See ya next time!");
    }

    private String readInput() {
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();
    }

    protected abstract void printPrompt();

    protected abstract void printHelpString();

    protected abstract Presenter presenterFactory();
}
