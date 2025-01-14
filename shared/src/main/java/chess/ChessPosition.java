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


    // --------------------- DIRECTIONAL FUNCTIONS FOR ADJACENT SQUARES
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

    /**
     * @return the square directly above this one if it exists,
     * otherwise return null
     */
    public ChessPosition getTop() {
        if (row < 8) {
            return new ChessPosition(row + 1, col);
        }
        return null;
    }

    /**
     * @return the square directly below this one if it exists,
     * otherwise return null
     */
    public ChessPosition getBottom() {
        if (row > 1) {
            return new ChessPosition(row - 1, col);
        }
        return null;
    }

    /**
     * @return the square directly left of this one if it exists,
     * otherwise return null
     */
    public ChessPosition getLeft() {
        if (col < 8) {
            return new ChessPosition(row, col + 1);
        }
        return null;
    }

    /**
     * @return the square directly right of this one if it exists,
     * otherwise return null
     */
    public ChessPosition getRight() {
        if (col > 1) {
            return new ChessPosition(row, col - 1);
        }
        return null;
    }

    /**
     * @param rowOffset: the integer for the offset from the current row (between -7 and 7)
     * @param colOffset: the integer for the offset from the current column (between -7 and 7)
     * @return the ChessPosition that is offset from this position by the specified number of rows and columns.
     */
    public ChessPosition getSquareByOffset(int rowOffset, int colOffset) {
        int newRow = row + rowOffset;
        int newCol = col + colOffset;
        if (newRow > 0 && newRow < 9 && newCol > 0 && newCol < 9) {
            return new ChessPosition(newRow, newCol);
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
