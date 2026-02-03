package movecalculators;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.Collection;
import java.util.HashSet;

public class BishopMoveCalculator implements MoveCalculator {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new HashSet<>();
        for (ChessPosition p : getMovesInDirection(myPosition, board, 1, 1)) {
            moves.add(new ChessMove(myPosition, p, null));
        }
        for (ChessPosition p : getMovesInDirection(myPosition, board, 1, -1)) {
            moves.add(new ChessMove(myPosition, p, null));
        }
        for (ChessPosition p : getMovesInDirection(myPosition, board, -1, 1)) {
            moves.add(new ChessMove(myPosition, p, null));
        }
        for (ChessPosition p : getMovesInDirection(myPosition, board, -1, -1)) {
            moves.add(new ChessMove(myPosition, p, null));
        }
        return moves;
    }
}
