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
                if (board.getPiece(top) == null) {
                    moves.add(new ChessMove(myPosition, top, ChessPiece.PieceType.QUEEN));
                    moves.add(new ChessMove(myPosition, top, ChessPiece.PieceType.ROOK));
                    moves.add(new ChessMove(myPosition, top, ChessPiece.PieceType.KNIGHT));
                    moves.add(new ChessMove(myPosition, top, ChessPiece.PieceType.BISHOP));
                }
                ChessPosition topRight = myPosition.getTopRight();
                // if top right space contains a different-colored piece, capture and promote
                if (topRight != null && board.getPiece(topRight) != null) {
                    if (board.getPiece(topRight).getTeamColor() != currentPiece.getTeamColor()) {
                        moves.add(new ChessMove(myPosition, topRight, ChessPiece.PieceType.QUEEN));
                        moves.add(new ChessMove(myPosition, topRight, ChessPiece.PieceType.ROOK));
                        moves.add(new ChessMove(myPosition, topRight, ChessPiece.PieceType.KNIGHT));
                        moves.add(new ChessMove(myPosition, topRight, ChessPiece.PieceType.BISHOP));
                    }
                }
                ChessPosition topLeft = myPosition.getTopLeft();
                // if top left space contains a different-colored piece, capture and promote
                if (topLeft != null && board.getPiece(topLeft) != null) {
                    if (board.getPiece(topLeft).getTeamColor() != currentPiece.getTeamColor()) {
                        moves.add(new ChessMove(myPosition, topLeft, ChessPiece.PieceType.QUEEN));
                        moves.add(new ChessMove(myPosition, topLeft, ChessPiece.PieceType.ROOK));
                        moves.add(new ChessMove(myPosition, topLeft, ChessPiece.PieceType.KNIGHT));
                        moves.add(new ChessMove(myPosition, topLeft, ChessPiece.PieceType.BISHOP));
                    }
                }
            } else {
                // move upward, capture upward diagonally, or option to move forward twice if on row 2
                ChessPosition topRight = myPosition.getTopRight();
                // if top right space contains a different-colored piece, capture
                if (topRight != null && board.getPiece(topRight) != null) {
                    if (board.getPiece(topRight).getTeamColor() != currentPiece.getTeamColor()) {
                        moves.add(new ChessMove(myPosition, topRight, null));
                    }
                }

                ChessPosition topLeft = myPosition.getTopLeft();
                // if top left space contains a different-colored piece, capture
                if (topLeft != null && board.getPiece(topLeft) != null) {
                    if (board.getPiece(topLeft).getTeamColor() != currentPiece.getTeamColor()) {
                        moves.add(new ChessMove(myPosition, topLeft, null));
                    }
                }

                ChessPosition top = myPosition.getTop();
                // if upper space is null, move up (and option to move up two spaces if on row 2)
                if (board.getPiece(top) == null) {
                    moves.add(new ChessMove(myPosition, top, null));
                    if (row == 2) {
                        ChessPosition upTwo = myPosition.getSquareByOffset(2, 0);
                        if (board.getPiece(upTwo) == null) {
                            moves.add(new ChessMove(myPosition, upTwo, null));
                        }
                    }
                }
            }
        }

        // dealing with a black pawn = moving and capturing downward
        if (currentPiece.getTeamColor() == ChessGame.TeamColor.BLACK) {
            if (row == 2) {
                // check for promotion
                ChessPosition bottomRight = myPosition.getBottomRight();
                if (bottomRight != null && board.getPiece(bottomRight) != null) {
                    if (board.getPiece(bottomRight).getTeamColor() != currentPiece.getTeamColor()) {
                        moves.add(new ChessMove(myPosition, bottomRight, ChessPiece.PieceType.QUEEN));
                        moves.add(new ChessMove(myPosition, bottomRight, ChessPiece.PieceType.ROOK));
                        moves.add(new ChessMove(myPosition, bottomRight, ChessPiece.PieceType.KNIGHT));
                        moves.add(new ChessMove(myPosition, bottomRight, ChessPiece.PieceType.BISHOP));
                    }
                }
                ChessPosition bottomLeft = myPosition.getBottomLeft();
                if (bottomLeft != null && board.getPiece(bottomLeft) != null) {
                    if (board.getPiece(bottomLeft).getTeamColor() != currentPiece.getTeamColor()) {
                        moves.add(new ChessMove(myPosition, bottomLeft, ChessPiece.PieceType.QUEEN));
                        moves.add(new ChessMove(myPosition, bottomLeft, ChessPiece.PieceType.ROOK));
                        moves.add(new ChessMove(myPosition, bottomLeft, ChessPiece.PieceType.KNIGHT));
                        moves.add(new ChessMove(myPosition, bottomLeft, ChessPiece.PieceType.BISHOP));
                    }
                }
                ChessPosition bottom = myPosition.getBottom();
                if (board.getPiece(bottom) == null) {
                    moves.add(new ChessMove(myPosition, bottom, ChessPiece.PieceType.QUEEN));
                    moves.add(new ChessMove(myPosition, bottom, ChessPiece.PieceType.ROOK));
                    moves.add(new ChessMove(myPosition, bottom, ChessPiece.PieceType.KNIGHT));
                    moves.add(new ChessMove(myPosition, bottom, ChessPiece.PieceType.BISHOP));
                }
            } else {
                // move normally or capture downward; option to move down 2 spaces if on row 7
                ChessPosition bottomRight = myPosition.getBottomRight();
                if (bottomRight != null && board.getPiece(bottomRight) != null) {
                    if (board.getPiece(bottomRight).getTeamColor() != currentPiece.getTeamColor()) {
                        moves.add(new ChessMove(myPosition, bottomRight, null));
                    }
                }
                ChessPosition bottomLeft = myPosition.getBottomLeft();
                if (bottomLeft != null && board.getPiece(bottomLeft) != null) {
                    if (board.getPiece(bottomLeft).getTeamColor() != currentPiece.getTeamColor()) {
                        moves.add(new ChessMove(myPosition, bottomLeft, null));
                    }
                }
                ChessPosition bottom = myPosition.getBottom();
                if (board.getPiece(bottom) == null) {
                    moves.add(new ChessMove(myPosition, bottom, null));
                    if (row == 7) {
                        ChessPosition downTwo = myPosition.getSquareByOffset(-2, 0);
                        if (board.getPiece(downTwo) == null) {
                            moves.add(new ChessMove(myPosition, downTwo, null));
                        }
                    }
                }
            }
        }

        return moves;
    }
}
