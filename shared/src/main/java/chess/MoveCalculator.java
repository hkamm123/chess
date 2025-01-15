package chess;

import java.util.ArrayList;
import java.util.Collection;

public interface MoveCalculator {
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition);

    default Collection<ChessMove> getMovesToTopRight(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new ArrayList<>();
        ChessPiece currentPiece = board.getPiece(position);

        ChessPosition topRight = position.getTopRight();
        while (topRight != null) {
            if (board.getPiece(topRight) != null) {
                ChessPiece obstructionPiece = board.getPiece(topRight);
                if (obstructionPiece.getTeamColor() != currentPiece.getTeamColor()) {
                    moves.add(new ChessMove(position, topRight, null));
                }
                break;
            }
            moves.add(new ChessMove(position, topRight, null));
            topRight = topRight.getTopRight();
        }
        return moves;
    }

    default ChessMove checkSquare(ChessBoard board, ChessPosition position, ChessPosition squareToCheck) {
        ChessPiece currentPiece = board.getPiece(position);
        if (squareToCheck == null) {
            return null;
        }
        if (board.getPiece(squareToCheck) != null) {
            ChessPiece obstructionPiece = board.getPiece(squareToCheck);
            if (obstructionPiece.getTeamColor() != currentPiece.getTeamColor()){
                return new ChessMove(position, squareToCheck, null);
            } else {
                return null;
            }
        }

        return new ChessMove(position, squareToCheck, null);
    }

    default Collection<ChessMove> getMovesToTopLeft(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new ArrayList<>();
        ChessPiece currentPiece = board.getPiece(position);

        ChessPosition topLeft = position.getTopLeft();
        while (topLeft != null) {
            if (board.getPiece(topLeft) != null) {
                ChessPiece obstructionPiece = board.getPiece(topLeft);
                if (obstructionPiece.getTeamColor() != currentPiece.getTeamColor()) {
                    moves.add(new ChessMove(position, topLeft, null));
                }
                break;
            }
            moves.add(new ChessMove(position, topLeft, null));
            topLeft = topLeft.getTopLeft();
        }
        return moves;
    }

    default Collection<ChessMove> getMovesToBottomRight(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new ArrayList<>();
        ChessPiece currentPiece = board.getPiece(position);

        ChessPosition bottomRight = position.getBottomRight();
        while (bottomRight != null) {
            if (board.getPiece(bottomRight) != null) {
                ChessPiece obstructionPiece = board.getPiece(bottomRight);
                if (obstructionPiece.getTeamColor() != currentPiece.getTeamColor()) {
                    moves.add(new ChessMove(position, bottomRight, null));
                }
                break;
            }
            moves.add(new ChessMove(position, bottomRight, null));
            bottomRight = bottomRight.getBottomRight();
        }
        return moves;
    }

    default Collection<ChessMove> getMovesToBottomLeft(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new ArrayList<>();
        ChessPiece currentPiece = board.getPiece(position);

        ChessPosition bottomLeft = position.getBottomLeft();
        while (bottomLeft != null) {
            if (board.getPiece(bottomLeft) != null) {
                ChessPiece obstructionPiece = board.getPiece(bottomLeft);
                if (obstructionPiece.getTeamColor() != currentPiece.getTeamColor()) {
                    moves.add(new ChessMove(position, bottomLeft, null));
                }
                break;
            }
            moves.add(new ChessMove(position, bottomLeft, null));
            bottomLeft = bottomLeft.getBottomLeft();
        }
        return moves;
    }

    default Collection<ChessMove> getMovesToTop(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new ArrayList<>();
        ChessPiece currentPiece = board.getPiece(position);

        ChessPosition top = position.getTop();
        while (top != null) {
            if (board.getPiece(top) != null) {
                ChessPiece obstructionPiece = board.getPiece(top);
                if (obstructionPiece.getTeamColor() != currentPiece.getTeamColor()) {
                    moves.add(new ChessMove(position, top, null));
                }
                break;
            }
            moves.add(new ChessMove(position, top, null));
            top = top.getTop();
        }
        return moves;
    }

    default Collection<ChessMove> getMovesToBottom(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new ArrayList<>();
        ChessPiece currentPiece = board.getPiece(position);
        ChessPosition bottom = position.getBottom();
        while (bottom != null) {
            if (board.getPiece(bottom) != null) {
                ChessPiece obstructionPiece = board.getPiece(bottom);
                if (obstructionPiece.getTeamColor() != currentPiece.getTeamColor()) {
                    moves.add(new ChessMove(position, bottom, null));
                }
                break;
            }
            moves.add(new ChessMove(position, bottom, null));
            bottom = bottom.getBottom();
        }
        return moves;
    }

    default Collection<ChessMove> getMovesToRight(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new ArrayList<>();
        ChessPiece currentPiece = board.getPiece(position);
        ChessPosition right = position.getRight();
        while (right != null) {
            if (board.getPiece(right) != null) {
                ChessPiece obstructionPiece = board.getPiece(right);
                if (obstructionPiece.getTeamColor() != currentPiece.getTeamColor()) {
                    moves.add(new ChessMove(position, right, null));
                }
                break;
            }
            moves.add(new ChessMove(position, right, null));
            right = right.getRight();
        }
        return moves;
    }

    default Collection<ChessMove> getMovesToLeft(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new ArrayList<>();
        ChessPiece currentPiece = board.getPiece(position);
        ChessPosition left = position.getLeft();
        while (left != null) {
            if (board.getPiece(left) != null) {
                ChessPiece obstructionPiece = board.getPiece(left);
                if (obstructionPiece.getTeamColor() != currentPiece.getTeamColor()) {
                    moves.add(new ChessMove(position, left, null));
                }
                break;
            }
            moves.add(new ChessMove(position, left, null));
            left = left.getLeft();
        }
        return moves;
    }
}
