package chess;

import java.util.Objects;
import java.util.Optional;

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
     * @return an optional which will contain the square to the top right of this one (if it exists)
     */
    public Optional<ChessPosition> getTopRight() {
        return getSquareByOffset(1, 1);
    }

    /**
     * @return an optional which will contain the square to the top left of this one (if it exists)
     */
    public Optional<ChessPosition> getTopLeft() {
        return getSquareByOffset(1, -1);
    }

    /**
     * @return an optional which will contain the square to the bottom right of this one (if it exists)
     */
    public Optional<ChessPosition> getBottomRight() {
        return getSquareByOffset(-1, 1);
    }

    /**
     * @return an optional which will contain the square to the bottom left of this one (if it exists)
     */
    public Optional<ChessPosition> getBottomLeft() {
        return getSquareByOffset(-1, -1);
    }

    /**
     * @return an optional which will contain the square to the top of this one (if it exists)
     */
    public Optional<ChessPosition> getTop() {
        return getSquareByOffset(1, 0);
    }

    /**
     * @return an optional which will contain the square to the bottom of this one (if it exists)
     */
    public Optional<ChessPosition> getBottom() {
        return getSquareByOffset(-1, 0);
    }

    /**
     * @return an optional which will contain the square to the left of this one (if it exists)
     */
    public Optional<ChessPosition> getLeft() {
        return getSquareByOffset(0, -1);
    }

    /**
     * @return an optional which will contain the square to the right of this one (if it exists)
     */
    public Optional<ChessPosition> getRight() {
        return getSquareByOffset(0, 1);
    }

    /**
     * @param rowOffset: the integer for the offset from the current row (between -7 and 7)
     * @param colOffset: the integer for the offset from the current column (between -7 and 7)
     * @return the ChessPosition that is offset from this position by the specified number of rows and columns.
     */
    public Optional<ChessPosition> getSquareByOffset(int rowOffset, int colOffset) {
        int newRow = row + rowOffset;
        int newCol = col + colOffset;
        if (newRow > 0 && newRow < 9 && newCol > 0 && newCol < 9) {
            return Optional.of(new ChessPosition(newRow, newCol));
        }
        return Optional.empty();
    }

    // --------------------------- OVERRIDES

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
        String colStr = switch(col) {
            case 1 -> "a";
            case 2 -> "b";
            case 3 -> "c";
            case 4 -> "d";
            case 5 -> "e";
            case 6 -> "f";
            case 7 -> "g";
            case 8 -> "h";
            default -> "out of bounds";
        };
        return " " + colStr + row + " ";
    }
}
