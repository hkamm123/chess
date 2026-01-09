package moveCalculators;

import chess.*;

import java.util.Collection;
import java.util.HashSet;

import static chess.ChessGame.TeamColor.*;
import static chess.ChessPiece.PieceType.*;

public class PawnMoveCalculator implements MoveCalculator {
    private final Collection<ChessMove> moves = new HashSet<>();
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessPosition> possiblePositions = new HashSet<>();
        ChessPiece myPiece = board.getPiece(myPosition);
        if (myPiece == null) {
            return new HashSet<>();
        }
        ChessPosition diag1 = null;
        ChessPosition diag2 = null;
        if (myPiece.getTeamColor() == WHITE) {
            // add the square directly above
            possiblePositions.add(getSquareByOffset(myPosition, 1, 0));
            // if the piece is on the starting row, add the square 2 squares above
            if (myPosition.getRow() == 2 &&
                    board.getPiece(getSquareByOffset(myPosition, 1, 0)) == null) {
                possiblePositions.add(getSquareByOffset(myPosition, 2, 0));
            }
            // add the diagonal squares if they are occupied by enemy pieces
            diag1 = getSquareByOffset(myPosition, 1, 1);
            diag2 = getSquareByOffset(myPosition, 1, -1);
        } else if (myPiece.getTeamColor() == BLACK) {
            // add the square directly down
            possiblePositions.add(getSquareByOffset(myPosition, -1, 0));
            // if the piece is on its starting row, add the square 2 squares down
            if (myPosition.getRow() == 7 &&
                    board.getPiece(getSquareByOffset(myPosition, -1, 0)) == null) {
                possiblePositions.add(getSquareByOffset(myPosition, -2, 0));
            }
            // add the diagonal squares if they are occupied by enemy pieces
            diag1 = getSquareByOffset(myPosition, -1, 1);
            diag2 = getSquareByOffset(myPosition, -1, -1);
        }
        if (diag1 != null &&
                board.getPiece(diag1) != null &&
                board.getPiece(diag1).getTeamColor() != myPiece.getTeamColor()) {
            addMove(myPosition, diag1, myPiece.getTeamColor());
        }
        if (diag2 != null &&
                board.getPiece(diag2) != null &&
                board.getPiece(diag2).getTeamColor() != myPiece.getTeamColor()) {
            addMove(myPosition, diag2, myPiece.getTeamColor());
        }
        for (ChessPosition p : possiblePositions) {
            if (p != null && board.getPiece(p) == null) {
                addMove(myPosition, p, myPiece.getTeamColor());
            }
        }
        return moves;
    }

    private void addMove(ChessPosition start, ChessPosition end, ChessGame.TeamColor myColor) {
        if ((myColor == WHITE && end.getRow() == 8) || (myColor == BLACK && end.getRow() == 1)) {
            moves.add(new ChessMove(start, end, QUEEN));
            moves.add(new ChessMove(start, end, ROOK));
            moves.add(new ChessMove(start, end, BISHOP));
            moves.add(new ChessMove(start, end, KNIGHT));
        } else {
            moves.add(new ChessMove(start, end, null));
        }
    }
}
