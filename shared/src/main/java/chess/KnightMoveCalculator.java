package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

public class KnightMoveCalculator implements MoveCalculator {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        Optional<ChessPosition>[] squaresToCheck;
        squaresToCheck = new Optional[]{
                myPosition.getSquareByOffset(2, 1),
                myPosition.getSquareByOffset(2, -1),
                myPosition.getSquareByOffset(1, 2),
                myPosition.getSquareByOffset(1, -2),
                myPosition.getSquareByOffset(-2, 1),
                myPosition.getSquareByOffset(-2, -1),
                myPosition.getSquareByOffset(-1, 2),
                myPosition.getSquareByOffset(-1, -2),
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
