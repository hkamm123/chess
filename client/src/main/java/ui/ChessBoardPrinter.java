package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static chess.ChessPiece.PieceType.*;
import static ui.ChessBoardPrinter.Perspective.BLACK;
import static ui.ChessBoardPrinter.Perspective.WHITE;
import static ui.EscapeSequences.*;

public class ChessBoardPrinter {
    public enum Perspective {BLACK, WHITE;}

    private static final Map<ChessPiece.PieceType, String> PIECE_LETTERS = Map.of(
            PAWN, "P",
            QUEEN, "Q",
            KNIGHT, "N",
            BISHOP, "B",
            ROOK, "R",
            KING, "K"
    );

    public static void drawBoard(ChessBoard board, Perspective perspective) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        String[] headers = new String[0];
        int[] rowsInOrder = new int[0];
        int[] colsInOrder = new int[0];
        if (perspective == WHITE) {
            headers = new String[]{" ", "a", "b", "c", "d", "e", "f", "g", "h", " "};
            rowsInOrder = new int[]{8, 7, 6, 5, 4, 3, 2, 1};
            colsInOrder = new int[]{1, 2, 3, 4, 5, 6, 7, 8};
        } else if (perspective == BLACK) {
            headers = new String[]{" ", "h", "g", "f", "e", "d", "c", "b", "a", " "};
            rowsInOrder = new int[]{1, 2, 3, 4, 5, 6, 7, 8};
            colsInOrder = new int[]{8, 7, 6, 5, 4, 3, 2, 1};
        }

        out.print(ERASE_SCREEN);
        drawHeaders(out, headers);
        drawRows(out, board, rowsInOrder, colsInOrder);
        drawHeaders(out, headers);
    }

    private static void drawHeaders(PrintStream out, String[] headers) {
        for (String headerChar : headers) {
            drawHeaderBox(out, headerChar);
        }
        setDefaults(out);
        out.println();
    }

    private static void drawHeaderBox(PrintStream out, String character) {
        out.print(SET_BG_COLOR_LIGHT_GREY);
        out.print(SET_TEXT_COLOR_BLACK);
        drawBox(out, character);

    }

    private static void drawBox(PrintStream out, String character) {
        out.print(" " + character + " ");
    }

    private static void setDefaults(PrintStream out) {
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    private static void drawRows(PrintStream out, ChessBoard board, int[] rowOrder, int[] colOrder) {
        for (int i = 0; i < 7; i += 2) {
            drawRow(out, board, rowOrder[i], colOrder, SET_BG_COLOR_WHITE, SET_BG_COLOR_BLACK);
            drawRow(out, board, rowOrder[i + 1], colOrder, SET_BG_COLOR_BLACK, SET_BG_COLOR_WHITE);
        }
    }

    private static void drawRow(
            PrintStream out,
            ChessBoard board,
            int row,
            int[] colOrder,
            String firstColEscSeq,
            String secondColEscSeq
    ) {
        out.print(SET_BG_COLOR_LIGHT_GREY);
        drawRowBox(out, row);
        for (int i = 0; i < 7; i += 2) {
            ChessPiece firstPiece = board.getPiece(new ChessPosition(row, colOrder[i]));
            String firstPieceLetter;
            if (firstPiece != null) {
                firstPieceLetter = PIECE_LETTERS.get(firstPiece.getPieceType());
            } else {
                firstPieceLetter = " ";
            }
            ChessPiece secondPiece = board.getPiece(new ChessPosition(row, colOrder[i + 1]));
            String secondPieceLetter;
            if (secondPiece != null) {
                secondPieceLetter = PIECE_LETTERS.get(secondPiece.getPieceType());
            } else {
                secondPieceLetter = " ";
            }
            out.print(firstColEscSeq);
            setColorForPiece(out, firstPiece);
            drawBox(out, firstPieceLetter);

            out.print(secondColEscSeq);
            setColorForPiece(out, secondPiece);
            drawBox(out, secondPieceLetter);
        }
        out.print(SET_BG_COLOR_LIGHT_GREY);
        drawRowBox(out, row);
        setDefaults(out);
        out.println();
    }

    private static void setColorForPiece(PrintStream out, ChessPiece piece) {
        if (piece != null) {
            if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                out.print(SET_TEXT_COLOR_RED);
            } else if (piece.getTeamColor() == ChessGame.TeamColor.BLACK) {
                out.print(SET_TEXT_COLOR_BLUE);
            }
        }
    }

    private static void drawRowBox(PrintStream out, int row) {
        out.print(SET_TEXT_COLOR_BLACK);
        String rowCharacter = "";
        switch (row) {
            case 1 -> rowCharacter = "1";
            case 2 -> rowCharacter = "2";
            case 3 -> rowCharacter = "3";
            case 4 -> rowCharacter = "4";
            case 5 -> rowCharacter = "5";
            case 6 -> rowCharacter = "6";
            case 7 -> rowCharacter = "7";
            case 8 -> rowCharacter = "8";
        }
        drawBox(out, rowCharacter);
    }
}
