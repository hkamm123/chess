package chess;

import java.util.ArrayList;
import java.util.Collection;

public class RookMoveCalculator implements MoveCalculator {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        Collection<ChessMove> topMoves = getMovesToTop(board, myPosition);
        moves.addAll(topMoves);
        Collection<ChessMove> bottomMoves = getMovesToBottom(board, myPosition);
        moves.addAll(bottomMoves);
        Collection<ChessMove> leftMoves = getMovesToLeft(board, myPosition);
        moves.addAll(leftMoves);
        Collection<ChessMove> rightMoves = getMovesToRight(board, myPosition);
        moves.addAll(rightMoves);
        return moves;
    }
}
