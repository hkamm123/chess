package ui.view;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import static chess.ChessGame.TeamColor.BLACK;
import static chess.ChessGame.TeamColor.WHITE;
import static ui.EscapeSequences.*;

public class ChessBoardPrinter {
    public void printBoard(ChessBoard board, ChessGame.TeamColor perspective) {
        print(ERASE_SCREEN);
        if (perspective == BLACK) {
            for (int row = 1; row <= 8; row += 2) {
                for (int col = 8; col >= 1; col--) {
                    printSquare(board.getPiece(new ChessPosition(row, col)), (col % 2 == 0 ? WHITE : BLACK));
                }
                print(SET_BG_COLOR_DARK_GREY);
                print(" " + row + " \n");
                for (int col = 8; col >= 1; col--) {
                    printSquare(board.getPiece(new ChessPosition(row + 1, col)), (col % 2 == 0 ? BLACK : WHITE));
                }
                print(SET_BG_COLOR_DARK_GREY);
                print(" " + (row + 1) + " \n");
            }
            printHeaders(perspective);
        } else {
            for (int row = 8; row >= 1; row -= 2) {
                for (int col = 1; col <= 8; col++) {
                    printSquare(board.getPiece(new ChessPosition(row, col)), (col % 2 == 0 ? BLACK : WHITE));
                }
                print(SET_BG_COLOR_DARK_GREY);
                print(" " + row + " \n");

                for (int col = 1; col <= 8; col++) {
                    printSquare(board.getPiece(new ChessPosition(row - 1, col)), (col % 2 == 0 ? WHITE : BLACK));
                }
                print(SET_BG_COLOR_DARK_GREY);
                print(" " + (row - 1) + " \n");
            }
            printHeaders(perspective);
        }
    }

    private void printHeaders(ChessGame.TeamColor perspective) {
        print(SET_BG_COLOR_DARK_GREY);
        if (perspective == WHITE) {
            print(" a  b  c  d  e  f  g  h \n");
        } else {
            print(" h  g  f  e  d  c  b  a \n");
        }
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
