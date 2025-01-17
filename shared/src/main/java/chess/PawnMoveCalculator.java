package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

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
                Optional<ChessPosition> optionalTop = myPosition.getTop();
                // if upper space is empty, add all promotion pieces to possible moves
                if (optionalTop.isPresent() && isEmpty(board, optionalTop.get())) {
                    ChessPosition top = optionalTop.get();
                    moves.add(new ChessMove(myPosition, top, ChessPiece.PieceType.QUEEN));
                    moves.add(new ChessMove(myPosition, top, ChessPiece.PieceType.ROOK));
                    moves.add(new ChessMove(myPosition, top, ChessPiece.PieceType.KNIGHT));
                    moves.add(new ChessMove(myPosition, top, ChessPiece.PieceType.BISHOP));
                }
                Optional<ChessPosition> optionalTopRight = myPosition.getTopRight();
                // if top right space contains a different-colored piece, capture and promote
                if (optionalTopRight.isPresent() && isEnemy(board, myPosition, optionalTopRight.get())) {
                    ChessPosition topRight = optionalTopRight.get();
                    moves.add(new ChessMove(myPosition, topRight, ChessPiece.PieceType.QUEEN));
                    moves.add(new ChessMove(myPosition, topRight, ChessPiece.PieceType.ROOK));
                    moves.add(new ChessMove(myPosition, topRight, ChessPiece.PieceType.KNIGHT));
                    moves.add(new ChessMove(myPosition, topRight, ChessPiece.PieceType.BISHOP));
                }
                Optional<ChessPosition> optionalTopLeft = myPosition.getTopLeft();
                // if top left space contains a different-colored piece, capture and promote
                if (optionalTopLeft.isPresent() && isEnemy(board, myPosition, optionalTopLeft.get())) {
                    ChessPosition topLeft = optionalTopLeft.get();
                    moves.add(new ChessMove(myPosition, topLeft, ChessPiece.PieceType.QUEEN));
                    moves.add(new ChessMove(myPosition, topLeft, ChessPiece.PieceType.ROOK));
                    moves.add(new ChessMove(myPosition, topLeft, ChessPiece.PieceType.KNIGHT));
                    moves.add(new ChessMove(myPosition, topLeft, ChessPiece.PieceType.BISHOP));
                }
            } else {
                // move upward, capture upward diagonally, or option to move forward twice if on row 2
                Optional<ChessPosition> optionalTopRight = myPosition.getTopRight();
                // if top right space contains a different-colored piece, capture
                if (optionalTopRight.isPresent() && isEnemy(board, myPosition, optionalTopRight.get())) {
                    moves.add(new ChessMove(myPosition, optionalTopRight.get(), null));
                }

                Optional<ChessPosition> optionalTopLeft = myPosition.getTopLeft();
                // if top left space contains a different-colored piece, capture
                if (optionalTopLeft.isPresent() && isEnemy(board, myPosition, optionalTopLeft.get())) {
                    moves.add(new ChessMove(myPosition, optionalTopLeft.get(), null));
                }

                Optional<ChessPosition> optionalTop = myPosition.getTop();
                // if upper space is null, move up (and option to move up two spaces if on row 2)
                if (optionalTop.isPresent() && isEmpty(board, optionalTop.get())) {
                    moves.add(new ChessMove(myPosition, optionalTop.get(), null));
                    Optional<ChessPosition> optionalUpTwo = myPosition.getSquareByOffset(2, 0);
                    if (row == 2 && isEmpty(board, optionalUpTwo.get())) {
                        moves.add(new ChessMove(
                                myPosition,
                                optionalUpTwo.get(),
                                null));
                    }
                }
            }
        }

        // TODO: finish changing these methods to handle optionals
        // dealing with a black pawn = moving and capturing downward
        if (currentPiece.getTeamColor() == ChessGame.TeamColor.BLACK) {
            if (row == 2) {
                // check for promotion
                Optional<ChessPosition> optionalBottomRight = myPosition.getBottomRight();
                if (optionalBottomRight.isPresent() && isEnemy(board, myPosition, optionalBottomRight.get())) {
                    ChessPosition bottomRight = optionalBottomRight.get();
                    moves.add(new ChessMove(myPosition, bottomRight, ChessPiece.PieceType.QUEEN));
                    moves.add(new ChessMove(myPosition, bottomRight, ChessPiece.PieceType.ROOK));
                    moves.add(new ChessMove(myPosition, bottomRight, ChessPiece.PieceType.KNIGHT));
                    moves.add(new ChessMove(myPosition, bottomRight, ChessPiece.PieceType.BISHOP));
                }
                Optional<ChessPosition> optionalBottomLeft = myPosition.getBottomLeft();
                if (optionalBottomLeft.isPresent() && isEnemy(board, myPosition, optionalBottomLeft.get())) {
                    ChessPosition bottomLeft = optionalBottomLeft.get();
                    moves.add(new ChessMove(myPosition, bottomLeft, ChessPiece.PieceType.QUEEN));
                    moves.add(new ChessMove(myPosition, bottomLeft, ChessPiece.PieceType.ROOK));
                    moves.add(new ChessMove(myPosition, bottomLeft, ChessPiece.PieceType.KNIGHT));
                    moves.add(new ChessMove(myPosition, bottomLeft, ChessPiece.PieceType.BISHOP));
                }
                Optional<ChessPosition> optionalBottom = myPosition.getBottom();
                if (optionalBottom.isPresent() && isEmpty(board, optionalBottom.get())) {
                    ChessPosition bottom = optionalBottom.get();
                    moves.add(new ChessMove(myPosition, bottom, ChessPiece.PieceType.QUEEN));
                    moves.add(new ChessMove(myPosition, bottom, ChessPiece.PieceType.ROOK));
                    moves.add(new ChessMove(myPosition, bottom, ChessPiece.PieceType.KNIGHT));
                    moves.add(new ChessMove(myPosition, bottom, ChessPiece.PieceType.BISHOP));
                }
            } else {
                // move normally or capture downward; option to move down 2 spaces if on row 7
                Optional<ChessPosition> optionalBottomRight = myPosition.getBottomRight();
                if (optionalBottomRight.isPresent() && isEnemy(board, myPosition, optionalBottomRight.get())) {
                    moves.add(new ChessMove(myPosition, optionalBottomRight.get(), null));
                }

                Optional<ChessPosition> optionalBottomLeft = myPosition.getBottomLeft();
                if (optionalBottomLeft.isPresent() && isEnemy(board, myPosition, optionalBottomLeft.get())) {
                    moves.add(new ChessMove(myPosition, optionalBottomLeft.get(), null));
                }
                Optional<ChessPosition> optionalBottom = myPosition.getBottom();
                if (optionalBottom.isPresent() && isEmpty(board, optionalBottom.get())) {
                    moves.add(new ChessMove(myPosition, optionalBottom.get(), null));
                    if (row == 7) {
                        Optional<ChessPosition> optionalDownTwo = myPosition.getSquareByOffset(-2, 0);
                        if (optionalDownTwo.isPresent() && isEmpty(board, optionalDownTwo.get())) {
                            moves.add(new ChessMove(myPosition, optionalDownTwo.get(), null));
                        }
                    }
                }
            }
        }

        return moves;
    }
}
