package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BishopMoveCalculator implements MoveCalculator {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();

        Collection<ChessMove> topRightMoves = getMovesByOffset(board, myPosition, 1, 1);
        moves.addAll(topRightMoves);

        Collection<ChessMove> topLeftMoves = getMovesByOffset(board, myPosition, 1, -1);
        moves.addAll(topLeftMoves);

        Collection<ChessMove> bottomRightMoves = getMovesByOffset(board, myPosition, -1, 1);
        moves.addAll(bottomRightMoves);

        Collection<ChessMove> bottomLeftMoves = getMovesByOffset(board, myPosition, -1, -1);
        moves.addAll(bottomLeftMoves);

        return moves;
    }
}
