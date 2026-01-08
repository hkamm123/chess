package moveCalculators;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.Collection;
import java.util.HashSet;

public class KingMoveCalculator implements MoveCalculator {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new HashSet<>();
        ChessPiece myPiece = board.getPiece(myPosition);
        if (myPiece == null) {
            return moves;
        }

        ChessPosition[] possiblePositions = new ChessPosition[]{
                getSquareByOffset(myPosition, 1, 0),
                getSquareByOffset(myPosition, 1, 1),
                getSquareByOffset(myPosition, 0, 1),
                getSquareByOffset(myPosition, -1, 1),
                getSquareByOffset(myPosition, -1, 0),
                getSquareByOffset(myPosition, -1, -1),
                getSquareByOffset(myPosition, 0, -1),
                getSquareByOffset(myPosition, 1, -1)
        };
        for (ChessPosition p : possiblePositions) {
            if (p != null && checkSquare(p, board, myPiece.getTeamColor())) {
                moves.add(new ChessMove(myPosition, p, null));
            }
        }
        return moves;
    }
}
