package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KnightMoveCalculator implements MoveCalculator {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        ChessPosition[] squaresToCheck;
        squaresToCheck = new ChessPosition[]{
                myPosition.getSquareByOffset(2, 1),
                myPosition.getSquareByOffset(2, -1),
                myPosition.getSquareByOffset(1, 2),
                myPosition.getSquareByOffset(1, -2),
                myPosition.getSquareByOffset(-2, 1),
                myPosition.getSquareByOffset(-2, -1),
                myPosition.getSquareByOffset(-1, 2),
                myPosition.getSquareByOffset(-1, -2),
        };

        for (ChessPosition squareToCheck : squaresToCheck) {
            if (squareToCheck != null) {
                ChessMove move = checkSquare(board, myPosition, squareToCheck);
                if (move != null) {
                    moves.add(move);
                }
            }
        }

        return moves;
    }
}
