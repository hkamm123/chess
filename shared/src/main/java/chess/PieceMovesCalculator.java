package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PieceMovesCalculator {
    public static Collection<ChessMove> calculate(ChessBoard board, ChessPosition position) {
        ChessPiece currentPiece = board.getPiece(position);
        if (currentPiece.getPieceType() == ChessPiece.PieceType.BISHOP) {
            return bishop(board, position);
        } else if (currentPiece.getPieceType() == ChessPiece.PieceType.KNIGHT) {
            return knight(board, position);
        } else if (currentPiece.getPieceType() == ChessPiece.PieceType.QUEEN) {
            return queen(board, position);
        } else if (currentPiece.getPieceType() == ChessPiece.PieceType.KING) {
            return king(board, position);
        } else if (currentPiece.getPieceType() == ChessPiece.PieceType.PAWN) {
            return pawn(board, position);
        } else if (currentPiece.getPieceType() == ChessPiece.PieceType.ROOK) {
            return rook(board, position);
        }
        throw new IllegalArgumentException("Unknown piece type: " + currentPiece.getPieceType());
    }

    // --------------------- DIRECTIONAL HELPER FUNCTIONS FOR CHECKING SQUARES

    private static Collection<ChessMove> getMovesToTopRight(ChessBoard board, ChessPosition position) {
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

    private static Collection<ChessMove> getMovesToTopLeft(ChessBoard board, ChessPosition position) {
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

    private static Collection<ChessMove> getMovesToBottomRight(ChessBoard board, ChessPosition position) {
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

    private static Collection<ChessMove> getMovesToBottomLeft(ChessBoard board, ChessPosition position) {
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

    private static Collection<ChessMove> getMovesToTop(ChessBoard board, ChessPosition position) {
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

    private static Collection<ChessMove> getMovesToBottom(ChessBoard board, ChessPosition position) {
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

    public static Collection<ChessMove> getMovesToRight(ChessBoard board, ChessPosition position) {
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

    public static Collection<ChessMove> getMovesToLeft(ChessBoard board, ChessPosition position) {
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

    // ----------------------- PIECE FUNCTIONS FOR CALCULATING MOVES
    public static Collection<ChessMove> pawn(ChessBoard board, ChessPosition position) {
        throw new RuntimeException("Not Implemented");
    }
    public static Collection<ChessMove> rook(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new ArrayList<>();
        Collection<ChessMove> topMoves = getMovesToTop(board, position);
        moves.addAll(topMoves);
        Collection<ChessMove> bottomMoves = getMovesToBottom(board, position);
        moves.addAll(bottomMoves);
        Collection<ChessMove> leftMoves = getMovesToLeft(board, position);
        moves.addAll(leftMoves);
        Collection<ChessMove> rightMoves = getMovesToRight(board, position);
        moves.addAll(rightMoves);
        return moves;
    }
    public static Collection<ChessMove> bishop(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new ArrayList<>();

        Collection<ChessMove> topRightMoves = getMovesToTopRight(board, position);
        moves.addAll(topRightMoves);

        Collection<ChessMove> topLeftMoves = getMovesToTopLeft(board, position);
        moves.addAll(topLeftMoves);

        Collection<ChessMove> bottomRightMoves = getMovesToBottomRight(board, position);
        moves.addAll(bottomRightMoves);

        Collection<ChessMove> bottomLeftMoves = getMovesToBottomLeft(board, position);
        moves.addAll(bottomLeftMoves);

        return moves;
    }

    public static Collection<ChessMove> king(ChessBoard board, ChessPosition position) {
        throw new RuntimeException("Not Implemented");
    }
    public static Collection<ChessMove> queen(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new ArrayList<>();
        Collection<ChessMove> topMoves = getMovesToTop(board, position);
        Collection<ChessMove> bottomMoves = getMovesToBottom(board, position);
        Collection<ChessMove> leftMoves = getMovesToLeft(board, position);
        Collection<ChessMove> rightMoves = getMovesToRight(board, position);
        Collection<ChessMove> topRightMoves = getMovesToTopRight(board, position);
        Collection<ChessMove> topLeftMoves = getMovesToTopLeft(board, position);
        Collection<ChessMove> bottomRightMoves = getMovesToBottomRight(board, position);
        Collection<ChessMove> bottomLeftMoves = getMovesToBottomLeft(board, position);

        moves.addAll(topMoves);
        moves.addAll(bottomMoves);
        moves.addAll(leftMoves);
        moves.addAll(rightMoves);
        moves.addAll(topRightMoves);
        moves.addAll(topLeftMoves);
        moves.addAll(bottomRightMoves);
        moves.addAll(bottomLeftMoves);
        return moves;
    }
    public static Collection<ChessMove> knight(ChessBoard board, ChessPosition position) {
        throw new RuntimeException("Not Implemented");
    }
}
