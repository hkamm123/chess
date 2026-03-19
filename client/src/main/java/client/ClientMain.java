package client;

import chess.ChessBoard;
import ui.view.ChessBoardPrinter;
import ui.view.LoggedOutView;
import ui.view.View;

public class ClientMain {
    public static void main(String[] args) {
        // View view = new LoggedOutView();
        // System.out.println("♕ Oh hey there! Welcome to the 240 Chess Client. Type 'help' or a command to get started.");
        // view.run();
        ChessBoard board = new ChessBoard();
        board.resetBoard();
        new ChessBoardPrinter().printBoard(board);
    }
}
