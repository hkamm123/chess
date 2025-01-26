package chess;

import java.util.ArrayList;
import java.util.Collection;

public class RookMoveCalculator implements MoveCalculator {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        Collection<ChessMove> topMoves = getMovesByOffset(board, myPosition, 1, 0);
        moves.addAll(topMoves);
        Collection<ChessMove> bottomMoves = getMovesByOffset(board, myPosition, -1, 0);
        moves.addAll(bottomMoves);
        Collection<ChessMove> leftMoves = getMovesByOffset(board, myPosition, 0, -1);
        moves.addAll(leftMoves);
        Collection<ChessMove> rightMoves = getMovesByOffset(board, myPosition, 0, 1);
        moves.addAll(rightMoves);
        return moves;
    }
}
