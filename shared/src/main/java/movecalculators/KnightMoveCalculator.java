package movecalculators;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.Collection;
import java.util.HashSet;

public class KnightMoveCalculator implements MoveCalculator {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new HashSet<>();
        ChessPiece myPiece = board.getPiece(myPosition);
        if (myPiece == null) {
            return moves;
        }
        ChessPosition[] possiblePositions = new ChessPosition[]{
                getSquareByOffset(myPosition, 2, 1),
                getSquareByOffset(myPosition, 2, -1),
                getSquareByOffset(myPosition, 1, 2),
                getSquareByOffset(myPosition, 1, -2),
                getSquareByOffset(myPosition, -1, 2),
                getSquareByOffset(myPosition, -1, -2),
                getSquareByOffset(myPosition, -2, 1),
                getSquareByOffset(myPosition, -2, -1)
        };
        for (ChessPosition p : possiblePositions) {
            if (p != null && checkSquare(p, board, myPiece.getTeamColor())) {
                moves.add(new ChessMove(myPosition, p, null));
            }
        }
        return moves;
    }
}
