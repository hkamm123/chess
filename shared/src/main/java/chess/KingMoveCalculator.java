package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class KingMoveCalculator implements MoveCalculator {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        ChessPosition[] squaresToCheck;
        squaresToCheck = new ChessPosition[]{
                myPosition.getTop(),
                myPosition.getTopRight(),
                myPosition.getTopLeft(),
                myPosition.getBottom(),
                myPosition.getBottomRight(),
                myPosition.getBottomLeft(),
                myPosition.getLeft(),
                myPosition.getRight()
        };

        for (ChessPosition square : squaresToCheck) {
            ChessMove move = checkSquare(board, myPosition, square);
            if (move != null) {
                moves.add(move);
            }
        }

        return moves;
    }
}
