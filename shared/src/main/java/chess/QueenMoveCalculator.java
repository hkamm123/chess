package chess;

import java.util.ArrayList;
import java.util.Collection;

public class QueenMoveCalculator implements MoveCalculator {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        Collection<ChessMove> topMoves = getMovesByOffset(board, myPosition, 1, 0);
        Collection<ChessMove> bottomMoves = getMovesByOffset(board, myPosition, -1, 0);
        Collection<ChessMove> leftMoves = getMovesByOffset(board, myPosition, 0, -1);
        Collection<ChessMove> rightMoves = getMovesByOffset(board, myPosition, 0, 1);
        Collection<ChessMove> topRightMoves = getMovesByOffset(board, myPosition, 1, 1);
        Collection<ChessMove> topLeftMoves = getMovesByOffset(board, myPosition, 1, -1);
        Collection<ChessMove> bottomRightMoves = getMovesByOffset(board, myPosition, -1, 1);
        Collection<ChessMove> bottomLeftMoves = getMovesByOffset(board, myPosition, -1, -1);

        moves.addAll(topMoves);
        moves.addAll(bottomMoves);
        moves.addAll(leftMoves);
        moves.addAll(rightMoves);
        moves.addAll(topRightMoves);
        moves.addAll(topLeftMoves);
        moves.addAll(bottomRightMoves);
        moves.addAll(bottomLeftMoves);
        return moves;
    }
}
