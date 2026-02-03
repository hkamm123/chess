package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private TeamColor teamTurn;
    private ChessBoard board;
    private boolean isOver;

    public ChessGame() {
        setBoard(new ChessBoard());
        board.resetBoard();
        setTeamTurn(TeamColor.WHITE);
        isOver = false;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece movingPiece = board.getPiece(startPosition);
        if (movingPiece == null) {
            return null;
        }
        Collection<ChessMove> pieceMoves = movingPiece.pieceMoves(board, startPosition);
        Collection<ChessMove> validMoves = new HashSet<>();
        for (ChessMove m : pieceMoves) {
            if (isValidMove(m)) {
                validMoves.add(m);
            }
        }
        return validMoves;
    }

    /**
     * Makes the move "hypothetically" on a clone board and asserts that the move did not put its own king in check.
     * @param move the potential move
     * @return true if the move would not put its own king in check, false otherwise
     */
    private boolean isValidMove(ChessMove move) {
        // clone board and make move
        ChessGame cloneGame = this.copy();
        ChessBoard board = cloneGame.getBoard();
        ChessPiece movingPiece = board.getPiece(move.getStartPosition());
        if (movingPiece == null) {
            return false;
        }
        board.addPiece(move.getStartPosition(), null);
        board.addPiece(move.getEndPosition(), movingPiece);
        // check if the move put its own team in check
        return !cloneGame.isInCheck(movingPiece.getTeamColor());
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        if (validMoves(move.getStartPosition()) == null) {
            throw new InvalidMoveException("Invalid move: there's no piece at that position");
        }
        if (!validMoves(move.getStartPosition()).contains(move)) {
            throw new InvalidMoveException("Invalid move: " + move);
        }
        if (board.getPiece(move.getStartPosition()).getTeamColor() != teamTurn) {
            throw new InvalidMoveException("Invalid move: it's not that color's turn");
        }
        ChessPiece pieceToAdd = board.getPiece(move.getStartPosition());
        if (move.getPromotionPiece() != null) {
            pieceToAdd = new ChessPiece(pieceToAdd.getTeamColor(), move.getPromotionPiece());
        }
        board.addPiece(move.getEndPosition(), pieceToAdd);
        board.addPiece(move.getStartPosition(), null);
        teamTurn = switch(teamTurn) {
            case WHITE -> TeamColor.BLACK;
            case BLACK -> TeamColor.WHITE;
        };
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPiece currentPiece;
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                currentPiece = board.getPiece(new ChessPosition(i, j));
                if (pieceAttackingKing(currentPiece, board, new ChessPosition(i, j), teamColor)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean pieceAttackingKing(
            ChessPiece currentPiece,
            ChessBoard board,
            ChessPosition currentPosition,
            TeamColor kingColor
    ) {
        if (currentPiece != null && currentPiece.getTeamColor() != kingColor) {
            for (ChessMove m : currentPiece.pieceMoves(board, currentPosition)) {
                if (board.getPiece(m.getEndPosition()) != null &&
                        board.getPiece(m.getEndPosition()).equals(
                                new ChessPiece(kingColor, ChessPiece.PieceType.KING))) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if (!isInCheck(teamColor)) {
            return false;
        }
        return teamHasValidMoves(teamColor);
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (isInCheck(teamColor) || isInCheckmate(teamColor)) {
            return false;
        }
        return teamHasValidMoves(teamColor);
    }

    private boolean teamHasValidMoves(TeamColor teamColor) {
        ChessPiece currentPiece;
        for (int i = 0; i <= 8; i++) {
            for (int j = 0; j <= 8; j++) {
                currentPiece = board.getPiece(new ChessPosition(i, j));
                if (currentPiece != null &&
                        currentPiece.getTeamColor() == teamColor &&
                        !validMoves(new ChessPosition(i, j)).isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    /**
     * @return true if the game is over, false otherwise
     */
    public boolean isOver() {
        return isOver;
    }

    /**
     * set the game status to game over
     */
    public void setOver() {
        isOver = true;
    }

    public ChessGame copy() {
        ChessGame copy = new ChessGame();
        copy.setBoard(this.board.copy());
        copy.setTeamTurn(this.teamTurn);
        return copy;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) object;
        return teamTurn == chessGame.teamTurn && Objects.equals(board, chessGame.board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamTurn, board);
    }
}
