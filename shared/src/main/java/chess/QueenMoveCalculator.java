package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class QueenMoveCalculator implements MoveCalculator {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        Collection<ChessMove> topMoves = getMovesToTop(board, myPosition);
        Collection<ChessMove> bottomMoves = getMovesToBottom(board, myPosition);
        Collection<ChessMove> leftMoves = getMovesToLeft(board, myPosition);
        Collection<ChessMove> rightMoves = getMovesToRight(board, myPosition);
        Collection<ChessMove> topRightMoves = getMovesToTopRight(board, myPosition);
        Collection<ChessMove> topLeftMoves = getMovesToTopLeft(board, myPosition);
        Collection<ChessMove> bottomRightMoves = getMovesToBottomRight(board, myPosition);
        Collection<ChessMove> bottomLeftMoves = getMovesToBottomLeft(board, myPosition);

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
