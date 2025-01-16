package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PawnMoveCalculator implements MoveCalculator {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        ChessPiece currentPiece = board.getPiece(myPosition);
        int row = myPosition.getRow();

        // dealing with a white pawn = moving and capturing upward
        if (currentPiece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            if (myPosition.getRow() == 7) {
                // check for promotion
                ChessPosition top = myPosition.getTop();
                // if upper space is empty, add all promotion pieces to possible moves
                if (isEmpty(board, top)) {
                    moves.add(new ChessMove(myPosition, top, ChessPiece.PieceType.QUEEN));
                    moves.add(new ChessMove(myPosition, top, ChessPiece.PieceType.ROOK));
                    moves.add(new ChessMove(myPosition, top, ChessPiece.PieceType.KNIGHT));
                    moves.add(new ChessMove(myPosition, top, ChessPiece.PieceType.BISHOP));
                }
                ChessPosition topRight = myPosition.getTopRight();
                // if top right space contains a different-colored piece, capture and promote
                if (isEnemy(board, myPosition, topRight)) {
                    moves.add(new ChessMove(myPosition, topRight, ChessPiece.PieceType.QUEEN));
                    moves.add(new ChessMove(myPosition, topRight, ChessPiece.PieceType.ROOK));
                    moves.add(new ChessMove(myPosition, topRight, ChessPiece.PieceType.KNIGHT));
                    moves.add(new ChessMove(myPosition, topRight, ChessPiece.PieceType.BISHOP));
                }
                ChessPosition topLeft = myPosition.getTopLeft();
                // if top left space contains a different-colored piece, capture and promote
                if (isEnemy(board, myPosition, topLeft)) {
                    moves.add(new ChessMove(myPosition, topLeft, ChessPiece.PieceType.QUEEN));
                    moves.add(new ChessMove(myPosition, topLeft, ChessPiece.PieceType.ROOK));
                    moves.add(new ChessMove(myPosition, topLeft, ChessPiece.PieceType.KNIGHT));
                    moves.add(new ChessMove(myPosition, topLeft, ChessPiece.PieceType.BISHOP));
                }
            } else {
                // move upward, capture upward diagonally, or option to move forward twice if on row 2
                ChessPosition topRight = myPosition.getTopRight();
                // if top right space contains a different-colored piece, capture
                if (isEnemy(board, myPosition, topRight)) {
                    moves.add(new ChessMove(myPosition, topRight, null));
                }

                ChessPosition topLeft = myPosition.getTopLeft();
                // if top left space contains a different-colored piece, capture
                if (isEnemy(board, myPosition, topLeft)) {
                    moves.add(new ChessMove(myPosition, topLeft, null));
                }

                ChessPosition top = myPosition.getTop();
                // if upper space is null, move up (and option to move up two spaces if on row 2)
                if (isEmpty(board, top)) {
                    moves.add(new ChessMove(myPosition, top, null));
                    if (row == 2 && isEmpty(board, myPosition.getSquareByOffset(2, 0))) {
                        moves.add(new ChessMove(
                                myPosition,
                                myPosition.getSquareByOffset(2, 0),
                                null));
                    }
                }
            }
        }

        // dealing with a black pawn = moving and capturing downward
        if (currentPiece.getTeamColor() == ChessGame.TeamColor.BLACK) {
            if (row == 2) {
                // check for promotion
                ChessPosition bottomRight = myPosition.getBottomRight();
                if (isEnemy(board, myPosition, bottomRight)) {
                    moves.add(new ChessMove(myPosition, bottomRight, ChessPiece.PieceType.QUEEN));
                    moves.add(new ChessMove(myPosition, bottomRight, ChessPiece.PieceType.ROOK));
                    moves.add(new ChessMove(myPosition, bottomRight, ChessPiece.PieceType.KNIGHT));
                    moves.add(new ChessMove(myPosition, bottomRight, ChessPiece.PieceType.BISHOP));
                }
                ChessPosition bottomLeft = myPosition.getBottomLeft();
                if (isEnemy(board, myPosition, bottomLeft)) {
                    moves.add(new ChessMove(myPosition, bottomLeft, ChessPiece.PieceType.QUEEN));
                    moves.add(new ChessMove(myPosition, bottomLeft, ChessPiece.PieceType.ROOK));
                    moves.add(new ChessMove(myPosition, bottomLeft, ChessPiece.PieceType.KNIGHT));
                    moves.add(new ChessMove(myPosition, bottomLeft, ChessPiece.PieceType.BISHOP));
                }
                ChessPosition bottom = myPosition.getBottom();
                if (isEmpty(board, bottom)) {
                    moves.add(new ChessMove(myPosition, bottom, ChessPiece.PieceType.QUEEN));
                    moves.add(new ChessMove(myPosition, bottom, ChessPiece.PieceType.ROOK));
                    moves.add(new ChessMove(myPosition, bottom, ChessPiece.PieceType.KNIGHT));
                    moves.add(new ChessMove(myPosition, bottom, ChessPiece.PieceType.BISHOP));
                }
            } else {
                // move normally or capture downward; option to move down 2 spaces if on row 7
                ChessPosition bottomRight = myPosition.getBottomRight();
                if (isEnemy(board, myPosition, bottomRight)) {
                    moves.add(new ChessMove(myPosition, bottomRight, null));
                }

                ChessPosition bottomLeft = myPosition.getBottomLeft();
                if (isEnemy(board, myPosition, bottomLeft)) {
                    moves.add(new ChessMove(myPosition, bottomLeft, null));
                }
                ChessPosition bottom = myPosition.getBottom();
                if (isEmpty(board, bottom)) {
                    moves.add(new ChessMove(myPosition, bottom, null));
                    if (row == 7) {
                        ChessPosition downTwo = myPosition.getSquareByOffset(-2, 0);
                        if (isEmpty(board, downTwo)) {
                            moves.add(new ChessMove(myPosition, downTwo, null));
                        }
                    }
                }
            }
        }

        return moves;
    }
}
