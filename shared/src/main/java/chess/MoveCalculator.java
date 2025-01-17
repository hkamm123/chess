package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

public interface MoveCalculator {
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition);


    // TODO: after fixing methods in ChessPosition, use Optionals here instead of null checks.

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

    default Collection<ChessMove> getMovesToTopRight(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new ArrayList<>();

        Optional<ChessPosition> optionalTopRight = position.getTopRight();
        while (optionalTopRight.isPresent()) {
            ChessPosition topRight = optionalTopRight.get();
            if (!isEmpty(board, topRight)) {
                if (isEnemy(board, topRight, position)) {
                    moves.add(new ChessMove(position, topRight, null));
                }
                break;
            }
            moves.add(new ChessMove(position, topRight, null));
            optionalTopRight = topRight.getTopRight();
        }
        return moves;
    }

    default Collection<ChessMove> getMovesToTopLeft(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new ArrayList<>();

        Optional<ChessPosition> optionalTopLeft = position.getTopLeft();
        while (optionalTopLeft.isPresent()) {
            ChessPosition topLeft = optionalTopLeft.get();
            if (!isEmpty(board, topLeft)) {
                if (isEnemy(board, position, topLeft)) {
                    moves.add(new ChessMove(position, topLeft, null));
                }
                break;
            }
            moves.add(new ChessMove(position, topLeft, null));
            optionalTopLeft = topLeft.getTopLeft();
        }
        return moves;
    }

    default Collection<ChessMove> getMovesToBottomRight(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new ArrayList<>();

        Optional<ChessPosition> optionalBottomRight = position.getBottomRight();
        while (optionalBottomRight.isPresent()) {
            ChessPosition bottomRight = optionalBottomRight.get();
            if (!isEmpty(board, bottomRight)) {
                if (isEnemy(board, position, bottomRight)) {
                    moves.add(new ChessMove(position, bottomRight, null));
                }
                break;
            }
            moves.add(new ChessMove(position, bottomRight, null));
            optionalBottomRight = bottomRight.getBottomRight();
        }
        return moves;
    }

    default Collection<ChessMove> getMovesToBottomLeft(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new ArrayList<>();

        Optional<ChessPosition> optionalBottomLeft = position.getBottomLeft();
        while (optionalBottomLeft.isPresent()) {
            ChessPosition bottomLeft = optionalBottomLeft.get();
            if (!isEmpty(board, bottomLeft)) {
                if (isEnemy(board, position, bottomLeft)) {
                    moves.add(new ChessMove(position, bottomLeft, null));
                }
                break;
            }
            moves.add(new ChessMove(position, bottomLeft, null));
            optionalBottomLeft = bottomLeft.getBottomLeft();
        }
        return moves;
    }

    default Collection<ChessMove> getMovesToTop(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new ArrayList<>();

        Optional<ChessPosition> optionalTop = position.getTop();
        while (optionalTop.isPresent()) {
            ChessPosition top = optionalTop.get();
            if (!isEmpty(board, top)) {
                if (isEnemy(board, position, top)) {
                    moves.add(new ChessMove(position, top, null));
                }
                break;
            }
            moves.add(new ChessMove(position, top, null));
            optionalTop = top.getTop();
        }
        return moves;
    }

    default Collection<ChessMove> getMovesToBottom(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new ArrayList<>();

        Optional<ChessPosition> optionalBottom = position.getBottom();
        while (optionalBottom.isPresent()) {
            ChessPosition bottom = optionalBottom.get();
            if (!isEmpty(board, bottom)) {
                if (isEnemy(board, position, bottom)) {
                    moves.add(new ChessMove(position, bottom, null));
                }
                break;
            }
            moves.add(new ChessMove(position, bottom, null));
            optionalBottom = bottom.getBottom();
        }
        return moves;
    }

    default Collection<ChessMove> getMovesToRight(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new ArrayList<>();

        Optional<ChessPosition> optionalRight = position.getRight();
        while (optionalRight.isPresent()) {
            ChessPosition right = optionalRight.get();
            if (!isEmpty(board, right)) {
                if (isEnemy(board, position, right)) {
                    moves.add(new ChessMove(position, right, null));
                }
                break;
            }
            moves.add(new ChessMove(position, right, null));
            optionalRight = right.getRight();
        }
        return moves;
    }

    default Collection<ChessMove> getMovesToLeft(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new ArrayList<>();

        Optional<ChessPosition> optionalLeft = position.getLeft();
        while (optionalLeft.isPresent()) {
            ChessPosition left = optionalLeft.get();
            if (!isEmpty(board, left)) {
                if (isEnemy(board, position, left)) {
                    moves.add(new ChessMove(position, left, null));
                }
                break;
            }
            moves.add(new ChessMove(position, left, null));
            optionalLeft = left.getLeft();
        }
        return moves;
    }
}
