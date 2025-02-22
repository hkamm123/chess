package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import static chess.ChessGame.TeamColor.*;
import static chess.ChessPiece.PieceType.*;

public class PawnMoveCalculator implements MoveCalculator {
    /**
     * Returns a collection containing either 4 moves or 1 move, based on whether/not the pawn should be promoted.
     *
     * @param startPosition the starting position of the pawn
     * @param endPosition   the position to which the pawn will move
     * @param promotionRow  the "end row" of the pawn (should be 8 if white, 1 if black)
     * @return collection of move(s)
     */
    private Collection<ChessMove> pawnAdd(ChessPosition startPosition, ChessPosition endPosition, int promotionRow) {
        Collection<ChessMove> moves = new ArrayList<>();
        if (endPosition.getRow() == promotionRow) {
            moves.add(new ChessMove(startPosition, endPosition, QUEEN));
            moves.add(new ChessMove(startPosition, endPosition, ROOK));
            moves.add(new ChessMove(startPosition, endPosition, KNIGHT));
            moves.add(new ChessMove(startPosition, endPosition, BISHOP));
        } else {
            moves.add(new ChessMove(startPosition, endPosition, null));
        }
        return moves;
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>(); // the moves collection which will be returned
        ChessPiece currentPiece = board.getPiece(myPosition); // the piece being evaluated
        int currentRow = myPosition.getRow(); // useful for checking if the pawn can move 2 spaces
        int promotionRow = 0; // this will be changed based on the color of the current piece (white=8, black=1)
        int startingRow = 0; // set to 2 if white, 7 if black
        Optional<ChessPosition> leftDiagonal = Optional.empty(); // top left for white, bottom left for black
        Optional<ChessPosition> rightDiagonal = Optional.empty(); // top right for white, bottom right for black
        Optional<ChessPosition> straight = Optional.empty(); // up for white, down for black
        int forwardDirection = 0; // set to 1 if white, -1 if black

        if (currentPiece.getTeamColor() == WHITE) {
            // setting the variables
            promotionRow = 8;
            leftDiagonal = myPosition.getSquareByOffset(1, -1);
            rightDiagonal = myPosition.getSquareByOffset(1, 1);
            straight = myPosition.getSquareByOffset(1, 0);
            startingRow = 2;
            forwardDirection = 1;
        } else if (currentPiece.getTeamColor() == BLACK) {
            // setting the variables
            promotionRow = 1;
            leftDiagonal = myPosition.getSquareByOffset(-1, -1);
            rightDiagonal = myPosition.getSquareByOffset(-1, 1);
            straight = myPosition.getSquareByOffset(-1, 0);
            startingRow = 7;
            forwardDirection = -1;
        }

        if (straight.isPresent() && isEmpty(board, straight.get())) {
            moves.addAll(pawnAdd(myPosition, straight.get(), promotionRow));
            if (currentRow == startingRow) {
                Optional<ChessPosition> straightTwo = straight.get().getSquareByOffset(forwardDirection, 0);
                if (straightTwo.isPresent() && isEmpty(board, straightTwo.get())) {
                    moves.addAll(pawnAdd(myPosition, straightTwo.get(), promotionRow));
                }
            }
        }

        if (rightDiagonal.isPresent() && isEnemy(board, myPosition, rightDiagonal.get())) {
            moves.addAll(pawnAdd(myPosition, rightDiagonal.get(), promotionRow));
        }

        if (leftDiagonal.isPresent() && isEnemy(board, myPosition, leftDiagonal.get())) {
            moves.addAll(pawnAdd(myPosition, leftDiagonal.get(), promotionRow));
        }

        return moves;
    }
}
