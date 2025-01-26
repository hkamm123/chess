package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

public interface MoveCalculator {
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition);

    /**
     * If the square checked contains an enemy piece or no piece, return
     * a ChessMove from the current square to the checked square. Does not take piece type
     * rules into account.
     * @param board the board which contains the squares being checked
     * @param position the current position
     * @param squareToCheck the position which will be moved to if the check succeeds
     * @return a ChessMove from position to squareToCheck with promotionPiece as null (if
     * the check is successful) or null.
     */
    default Optional<ChessMove> checkSquare(ChessBoard board, ChessPosition position, ChessPosition squareToCheck) {
        ChessPiece currentPiece = board.getPiece(position);
        if (squareToCheck == null) {
            return Optional.empty();
        }
        if (board.getPiece(squareToCheck) != null) {
            ChessPiece obstructionPiece = board.getPiece(squareToCheck);
            if (obstructionPiece.getTeamColor() != currentPiece.getTeamColor()){
                return Optional.of(new ChessMove(position, squareToCheck, null));
            } else {
                return Optional.empty();
            }
        }

        return Optional.of(new ChessMove(position, squareToCheck, null));
    }

    /**
     * Checks a square to see if it's empty.
     * @param board the board that contains squareToCheck
     * @param squareToCheck the square being checked
     * @return true if empty, false otherwise.
     */
    default Boolean isEmpty(ChessBoard board, ChessPosition squareToCheck) {
        return board.getPiece(squareToCheck) == null;
    }

    /**
     * Given a current position, checks another square to see if it contains a different-colored piece.
     * @param board the board containing the squares being checked.
     * @param currentSquare the square which contains the current piece.
     * @param squareToCheck the square which may/may not contain an enemy piece
     * @return true if squareToCheck contains a different-colored piece, false otherwise.
     */
    default Boolean isEnemy(ChessBoard board, ChessPosition currentSquare, ChessPosition squareToCheck) {
        if (currentSquare == null || squareToCheck == null) {
            return false;
        }
        ChessPiece currentPiece = board.getPiece(currentSquare);
        ChessPiece obstructionPiece = board.getPiece(squareToCheck);

        if (!isEmpty(board, squareToCheck)) {
            return currentPiece.getTeamColor() != obstructionPiece.getTeamColor();
        }
        return false;
    }

    default Collection<ChessMove> getMovesByOffset(ChessBoard board, ChessPosition position, int rowOffset, int colOffset) {
        Collection<ChessMove> moves = new ArrayList();

        Optional<ChessPosition> opSquare = position.getSquareByOffset(rowOffset, colOffset);
        while (opSquare.isPresent()) {
            if (!isEmpty(board, opSquare.get())) {
                if (isEnemy(board, opSquare.get(), position)) {
                    moves.add(new ChessMove(position, opSquare.get(), null));
                }
                break;
            }
            moves.add(new ChessMove(position, opSquare.get(), null));
            opSquare = opSquare.get().getSquareByOffset(rowOffset, colOffset);
        }
        return moves;
    }
}
