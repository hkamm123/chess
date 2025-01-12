package chess;

import java.util.Objects;

/**
 * Represents a single square position on a chess board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPosition {

    private final int row;
    private final int col;

    public ChessPosition(int row, int col) {
        this.row = row;
        this.col = col;
    }

    /**
     * @return which row this position is in
     * 1 codes for the bottom row
     */
    public int getRow() {
        return row;
    }

    /**
     * @return which column this position is in
     * 1 codes for the left row
     */
    public int getColumn() {
        return col;
    }

    /**
     * @return the top right diagonal square to the current position if
     * within the board, otherwise return null
     */
    public ChessPosition getTopRight() {
        if (row < 8 && col < 8) {
            return new ChessPosition(row + 1, col + 1);
        }
        return null;
    }

    /**
     * @return the top left diagonal square to the current position if
     * within the board, otherwise return null
     */
    public ChessPosition getTopLeft() {
        if (row < 8 && col > 1) {
            return new ChessPosition(row + 1, col - 1);
        }
        return null;
    }

    /**
     * @return the bottom right diagonal square to the current position if
     * within the board, otherwise return null
     */
    public ChessPosition getBottomRight() {
        if (row > 1 && col < 8) {
            return new ChessPosition(row - 1, col + 1);
        }
        return null;
    }

    /**
     * @return the bottom left diagonal square to the current position if
     * within the board, otherwise return null
     */
    public ChessPosition getBottomLeft() {
        if (row > 1 && col > 1) {
            return new ChessPosition(row - 1, col - 1);
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPosition that = (ChessPosition) o;
        return row == that.row && col == that.col;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }

    @Override
    public String toString() {
        return "ChessPosition{" +
                "row=" + row +
                ", col=" + col +
                '}';
    }
}
