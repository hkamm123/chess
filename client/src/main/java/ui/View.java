package ui;

import java.util.Scanner;

public abstract class View {
    public void run() {
        printPrompt();
        String nextInput = readInput();
        while (!nextInput.equals("quit")) {
            if (nextInput.equals("help") || nextInput.equals("h")) {
                printHelpString();
            } else {
                eval(nextInput);
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

    protected abstract void eval(String input);

    protected abstract void printHelpString();
}
