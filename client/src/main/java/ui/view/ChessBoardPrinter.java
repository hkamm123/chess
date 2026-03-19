package ui.view;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import static chess.ChessGame.TeamColor.BLACK;
import static chess.ChessGame.TeamColor.WHITE;
import static ui.EscapeSequences.*;

public class ChessBoardPrinter {
    public void printBoard(ChessBoard board) {
        print(ERASE_SCREEN);
        for (int row = 1; row <= 8; row += 2) {
            for (int col = 1; col <= 8; col++) {
                printSquare(board.getPiece(new ChessPosition(row, col)), (col % 2 == 0 ? WHITE : BLACK));
            }
            print(SET_BG_COLOR_DARK_GREY);
            print(" " + row + " \n");
            for (int col = 1; col <= 8; col++) {
                printSquare(board.getPiece(new ChessPosition(row + 1, col)), (col % 2 == 0 ? BLACK : WHITE));
            }
            print(SET_BG_COLOR_DARK_GREY);
            print(" " + (row + 1) + " \n");
        }
        printHeaders();
    }

    private void printHeaders() {
        print(SET_BG_COLOR_DARK_GREY);
        print(" a  b  c  d  e  f  g  h \n");
    }

    private void printSquare(ChessPiece piece, ChessGame.TeamColor backgroundColor) {
        // set the background color
        if (backgroundColor == BLACK) {
            print(SET_BG_COLOR_BLACK);
        } else {
            print(SET_BG_COLOR_DARK_GREY);
        }
        // print out the piece
        print(" ");
        print(piece != null ? piece.toString() : " ");
        print(" ");
    }

    private void print(String str) {
        System.out.print(str);
    }
}
