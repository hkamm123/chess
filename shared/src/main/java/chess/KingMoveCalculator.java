package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

public class KingMoveCalculator implements MoveCalculator {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        Optional<ChessPosition>[] squaresToCheck;
        squaresToCheck = new Optional[]{
                myPosition.getTop(),
                myPosition.getTopRight(),
                myPosition.getTopLeft(),
                myPosition.getBottom(),
                myPosition.getBottomRight(),
                myPosition.getBottomLeft(),
                myPosition.getLeft(),
                myPosition.getRight()
        };

        for (Optional<ChessPosition> optionalSquare : squaresToCheck) {
            if (optionalSquare.isPresent()) {
                Optional<ChessMove> optionalMove = checkSquare(board, myPosition, optionalSquare.get());
                optionalMove.ifPresent(moves::add);
            }
        }

        return moves;
    }
}
