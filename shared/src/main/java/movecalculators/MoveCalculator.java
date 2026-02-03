package movecalculators;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.Collection;
import java.util.HashSet;

public interface MoveCalculator {
    Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition);

    /**
     * Checks a potential destination square to see if the move is possible.
     * @param square the potential destination
     * @param board the board context
     * @param myColor the color of the piece being moved to the destination in question
     * @return true if square is empty or contains a piece of color opposite myColor, false otherwise
     */
    default boolean checkSquare(ChessPosition square, ChessBoard board, ChessGame.TeamColor myColor) {
        if (square == null) {
            return false;
        }
        return board.getPiece(square) == null || board.getPiece(square).getTeamColor() != myColor;
    }

    /**
     * Returns the square that is y rows and x columns away from square, where y = rowOffset and x = colOffset
     * @param square the starting ChessPosition
     * @param rowOffset y offset (positive is up and negative is down)
     * @param colOffset x offset (positive is right and negative is left)
     * @return new ChessPosition if exists, otherwise null
     */
    default ChessPosition getSquareByOffset(ChessPosition square, int rowOffset, int colOffset) {
        int newRow = square.getRow() + rowOffset;
        int newCol = square.getColumn() + colOffset;
        if (newRow < 1 || newRow > 8 || newCol < 1 || newCol > 8) {
            return null;
        }
        return new ChessPosition(newRow, newCol);
    }

    /**
     * Checks all squares in given direction, stops when the edge of the board or a piece of the same color is reached.
     * @param myPosition position of piece to be moved
     * @param board board context
     * @param rowOffset row offset direction (1 for up and -1 for down)
     * @param colOffset col offset direction (1 for right and -1 for left)
     * @return Collection of all valid positions in the given direction
     */
    default Collection<ChessPosition> getMovesInDirection(
            ChessPosition myPosition,
            ChessBoard board,
            int rowOffset,
            int colOffset) {
        Collection<ChessPosition> positions = new HashSet<>();
        if (board.getPiece(myPosition) == null) {
            return positions;
        }
        ChessGame.TeamColor myColor = board.getPiece(myPosition).getTeamColor();
        ChessPosition currentPosition = getSquareByOffset(myPosition, rowOffset, colOffset);
        while(currentPosition != null && board.getPiece(currentPosition) == null) {
            positions.add(currentPosition);
            currentPosition = getSquareByOffset(currentPosition, rowOffset, colOffset);
        }
        if (currentPosition != null && board.getPiece(currentPosition).getTeamColor() != myColor) {
            positions.add(currentPosition);
        }
        return positions;
    }
}
