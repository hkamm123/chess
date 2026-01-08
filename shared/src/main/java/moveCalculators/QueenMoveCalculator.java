package moveCalculators;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.Collection;
import java.util.HashSet;

public class QueenMoveCalculator implements MoveCalculator {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new HashSet<>();

        // moves upward
        for (ChessPosition p : getMovesInDirection(myPosition, board, 1, 0)) {
            moves.add(new ChessMove(myPosition, p, null));
        }
        // moves to top right
        for (ChessPosition p : getMovesInDirection(myPosition, board, 1, 1)) {
            moves.add(new ChessMove(myPosition, p, null));
        }
        // moves to right
        for (ChessPosition p : getMovesInDirection(myPosition, board, 0, 1)) {
            moves.add(new ChessMove(myPosition, p, null));
        }
        // moves to bottom right
        for (ChessPosition p : getMovesInDirection(myPosition, board, -1, 1)) {
            moves.add(new ChessMove(myPosition, p, null));
        }
        // moves downward
        for (ChessPosition p : getMovesInDirection(myPosition, board, -1, 0)) {
            moves.add(new ChessMove(myPosition, p, null));
        }
        // moves to bottom left
        for (ChessPosition p : getMovesInDirection(myPosition, board, -1, -1)) {
            moves.add(new ChessMove(myPosition, p, null));
        }
        // moves to left
        for (ChessPosition p : getMovesInDirection(myPosition, board, 0, -1)) {
            moves.add(new ChessMove(myPosition, p, null));
        }
        // moves to upper left
        for (ChessPosition p : getMovesInDirection(myPosition, board, 1, -1)) {
            moves.add(new ChessMove(myPosition, p, null));
        }
        return moves;
    }
}
