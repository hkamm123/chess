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

    // --------------------- HELPER METHOD FOR CHECKING ONE SQUARE
    // NOTE: this does not take piece type movement rules into account.
    // It simply checks whether there is a same-color piece in the square being checked.

    /**
     *
     * @param board: the current board
     * @param position: the position of the current piece
     * @param squareToCheck: the position to which the piece wants to move
     * @return a ChessMove if the move is valid, null otherwise
     */
    private static ChessMove checkSquare(ChessBoard board, ChessPosition position, ChessPosition squareToCheck) {
        ChessPiece currentPiece = board.getPiece(position);
        Collection<ChessMove> moves = new ArrayList<>();
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
        Collection<ChessMove> moves = new ArrayList<>();
        ChessPiece currentPiece = board.getPiece(position);
        int row = position.getRow();

        // dealing with a white pawn = moving and capturing upward
        if (currentPiece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            if (position.getRow() == 7) {
                // check for promotion
                ChessPosition top = position.getTop();
                // if upper space is empty, add all promotion pieces to possible moves
                if (board.getPiece(top) == null) {
                    moves.add(new ChessMove(position, top, ChessPiece.PieceType.QUEEN));
                    moves.add(new ChessMove(position, top, ChessPiece.PieceType.ROOK));
                    moves.add(new ChessMove(position, top, ChessPiece.PieceType.KNIGHT));
                    moves.add(new ChessMove(position, top, ChessPiece.PieceType.BISHOP));
                }
                ChessPosition topRight = position.getTopRight();
                // if top right space contains a different-colored piece, capture and promote
                if (topRight != null && board.getPiece(topRight) != null) {
                    if (board.getPiece(topRight).getTeamColor() != currentPiece.getTeamColor()) {
                        moves.add(new ChessMove(position, topRight, ChessPiece.PieceType.QUEEN));
                        moves.add(new ChessMove(position, topRight, ChessPiece.PieceType.ROOK));
                        moves.add(new ChessMove(position, topRight, ChessPiece.PieceType.KNIGHT));
                        moves.add(new ChessMove(position, topRight, ChessPiece.PieceType.BISHOP));
                    }
                }
                ChessPosition topLeft = position.getTopLeft();
                // if top left space contains a different-colored piece, capture and promote
                if (topLeft != null && board.getPiece(topLeft) != null) {
                    if (board.getPiece(topLeft).getTeamColor() != currentPiece.getTeamColor()) {
                        moves.add(new ChessMove(position, topLeft, ChessPiece.PieceType.QUEEN));
                        moves.add(new ChessMove(position, topLeft, ChessPiece.PieceType.ROOK));
                        moves.add(new ChessMove(position, topLeft, ChessPiece.PieceType.KNIGHT));
                        moves.add(new ChessMove(position, topLeft, ChessPiece.PieceType.BISHOP));
                    }
                }
            } else {
                // move upward, capture upward diagonally, or option to move forward twice if on row 2
                ChessPosition topRight = position.getTopRight();
                // if top right space contains a different-colored piece, capture
                if (topRight != null && board.getPiece(topRight) != null) {
                    if (board.getPiece(topRight).getTeamColor() != currentPiece.getTeamColor()) {
                        moves.add(new ChessMove(position, topRight, null));
                    }
                }

                ChessPosition topLeft = position.getTopLeft();
                // if top left space contains a different-colored piece, capture
                if (topLeft != null && board.getPiece(topLeft) != null) {
                    if (board.getPiece(topLeft).getTeamColor() != currentPiece.getTeamColor()) {
                        moves.add(new ChessMove(position, topLeft, null));
                    }
                }

                ChessPosition top = position.getTop();
                // if upper space is null, move up (and option to move up two spaces if on row 2)
                if (board.getPiece(top) == null) {
                    moves.add(new ChessMove(position, top, null));
                    if (row == 2) {
                        ChessPosition upTwo = position.getSquareByOffset(2, 0);
                        if (board.getPiece(upTwo) == null) {
                            moves.add(new ChessMove(position, upTwo, null));
                        }
                    }
                }
            }
        }

        // dealing with a black pawn = moving and capturing downward
        if (currentPiece.getTeamColor() == ChessGame.TeamColor.BLACK) {
            if (row == 2) {
                // check for promotion
                ChessPosition bottomRight = position.getBottomRight();
                if (bottomRight != null && board.getPiece(bottomRight) != null) {
                    if (board.getPiece(bottomRight).getTeamColor() != currentPiece.getTeamColor()) {
                        moves.add(new ChessMove(position, bottomRight, ChessPiece.PieceType.QUEEN));
                        moves.add(new ChessMove(position, bottomRight, ChessPiece.PieceType.ROOK));
                        moves.add(new ChessMove(position, bottomRight, ChessPiece.PieceType.KNIGHT));
                        moves.add(new ChessMove(position, bottomRight, ChessPiece.PieceType.BISHOP));
                    }
                }
                ChessPosition bottomLeft = position.getBottomLeft();
                if (bottomLeft != null && board.getPiece(bottomLeft) != null) {
                    if (board.getPiece(bottomLeft).getTeamColor() != currentPiece.getTeamColor()) {
                        moves.add(new ChessMove(position, bottomLeft, ChessPiece.PieceType.QUEEN));
                        moves.add(new ChessMove(position, bottomLeft, ChessPiece.PieceType.ROOK));
                        moves.add(new ChessMove(position, bottomLeft, ChessPiece.PieceType.KNIGHT));
                        moves.add(new ChessMove(position, bottomLeft, ChessPiece.PieceType.BISHOP));
                    }
                }
                ChessPosition bottom = position.getBottom();
                if (board.getPiece(bottom) == null) {
                    moves.add(new ChessMove(position, bottom, ChessPiece.PieceType.QUEEN));
                    moves.add(new ChessMove(position, bottom, ChessPiece.PieceType.ROOK));
                    moves.add(new ChessMove(position, bottom, ChessPiece.PieceType.KNIGHT));
                    moves.add(new ChessMove(position, bottom, ChessPiece.PieceType.BISHOP));
                }
            } else {
                // move normally or capture downward; option to move down 2 spaces if on row 7
                ChessPosition bottomRight = position.getBottomRight();
                if (bottomRight != null && board.getPiece(bottomRight) != null) {
                    if (board.getPiece(bottomRight).getTeamColor() != currentPiece.getTeamColor()) {
                        moves.add(new ChessMove(position, bottomRight, null));
                    }
                }
                ChessPosition bottomLeft = position.getBottomLeft();
                if (bottomLeft != null && board.getPiece(bottomLeft) != null) {
                    if (board.getPiece(bottomLeft).getTeamColor() != currentPiece.getTeamColor()) {
                        moves.add(new ChessMove(position, bottomLeft, null));
                    }
                }
                ChessPosition bottom = position.getBottom();
                if (board.getPiece(bottom) == null) {
                    moves.add(new ChessMove(position, bottom, null));
                    if (row == 7) {
                        ChessPosition downTwo = position.getSquareByOffset(-2, 0);
                        if (board.getPiece(downTwo) == null) {
                            moves.add(new ChessMove(position, downTwo, null));
                        }
                    }
                }
            }
        }

        return moves;
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
        Collection<ChessMove> moves = new ArrayList<>();
        ChessPosition[] squaresToCheck;
        squaresToCheck = new ChessPosition[]{
                position.getTop(),
                position.getTopRight(),
                position.getTopLeft(),
                position.getBottom(),
                position.getBottomRight(),
                position.getBottomLeft(),
                position.getLeft(),
                position.getRight()
        };

        for (ChessPosition square : squaresToCheck) {
            ChessMove move = checkSquare(board, position, square);
            if (move != null) {
                moves.add(move);
            }
        }

        return moves;
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
        ChessPosition squareToCheck;
        Collection<ChessMove> moves = new ArrayList<>();
        ChessPosition[] squaresToCheck;
        squaresToCheck = new ChessPosition[]{
                position.getSquareByOffset(2, 1),
                position.getSquareByOffset(2, -1),
                position.getSquareByOffset(1, 2),
                position.getSquareByOffset(1, -2),
                position.getSquareByOffset(-2, 1),
                position.getSquareByOffset(-2, -1),
                position.getSquareByOffset(-1, 2),
                position.getSquareByOffset(-1, -2),
        };

        for (int i = 0; i < 8; i++) {
            squareToCheck = squaresToCheck[i];
            if (squareToCheck != null) {
                ChessMove move = checkSquare(board, position, squareToCheck);
                if (move != null) {
                    moves.add(move);
                }
            }
        }

        return moves;
    }
}
