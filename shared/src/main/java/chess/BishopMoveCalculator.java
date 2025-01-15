package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BishopMoveCalculator implements MoveCalculator {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();

        Collection<ChessMove> topRightMoves = getMovesToTopRight(board, myPosition);
        moves.addAll(topRightMoves);

        Collection<ChessMove> topLeftMoves = getMovesToTopLeft(board, myPosition);
        moves.addAll(topLeftMoves);

        Collection<ChessMove> bottomRightMoves = getMovesToBottomRight(board, myPosition);
        moves.addAll(bottomRightMoves);

        Collection<ChessMove> bottomLeftMoves = getMovesToBottomLeft(board, myPosition);
        moves.addAll(bottomLeftMoves);

        return moves;
    }
}
