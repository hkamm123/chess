package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private ChessBoard board = new ChessBoard();
    private TeamColor teamTurn = TeamColor.WHITE;

    public ChessGame() {
        this.board.resetBoard();
    }

    private ChessGame(ChessGame game) {
        this.board = game.board.clone();
        this.teamTurn = game.teamTurn;
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
        teamTurn = team;
    }



    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK;
    }
    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece currentPiece = board.getPiece(startPosition);
        if (currentPiece == null) { return null; }
        Collection<ChessMove> possibleMoves = currentPiece.pieceMoves(board, startPosition);

        ChessGame clonedGame;
        Iterator<ChessMove> moveIterator = possibleMoves.iterator();
        while (moveIterator.hasNext()) {
            ChessMove move = moveIterator.next();
            // clone the game
            clonedGame = new ChessGame(this);
            // make the move on the cloned game ?
            clonedGame.makeMoveWithoutChecking(move);
            // if the team is in check on the cloned game, remove the move from possibleMoves
            if (clonedGame.isInCheck(currentPiece.getTeamColor())) {
                moveIterator.remove();
            }
        }
        return possibleMoves;
    }

    private void makeMoveWithoutChecking(ChessMove move) {
        ChessPiece movingPiece = board.getPiece(move.getStartPosition());
        if (move.getPromotionPiece() != null) {
            board.addPiece(move.getEndPosition(), new ChessPiece(teamTurn, move.getPromotionPiece()));
        } else {
            board.addPiece(move.getEndPosition(), movingPiece);
        }
        board.removePiece(move.getStartPosition());
        toggleTeam();
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        Collection<ChessMove> validMoves = validMoves(move.getStartPosition());
        ChessPiece movingPiece = board.getPiece(move.getStartPosition());

        if (movingPiece != null && validMoves.contains(move) && movingPiece.getTeamColor() == teamTurn) {
            makeMoveWithoutChecking(move);
        } else {
            throw new InvalidMoveException("Invalid move: " + move);
        }
    }

    private void toggleTeam() {
        if (teamTurn == TeamColor.BLACK) {
            setTeamTurn(TeamColor.WHITE);
        } else if (teamTurn == TeamColor.WHITE) {
            setTeamTurn(TeamColor.BLACK);
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        TeamColor enemyColor = TeamColor.BLACK;
        if (teamColor == TeamColor.BLACK) {
            enemyColor = TeamColor.WHITE;
        }
        Collection<ChessPosition> enemyPositions = getAllPositionsOfTeam(enemyColor);
        ChessPosition kingPosition = getKingPosition(teamColor);
        ChessPiece enemyPiece;
        for (ChessPosition pos : enemyPositions) {
            enemyPiece = board.getPiece(pos);
            for (ChessMove mov : enemyPiece.pieceMoves(board, pos)) {
                if (mov.getEndPosition().equals(kingPosition)) {
                    return true;
                }
            }
        }
        return false;
    }

    private ChessPosition getKingPosition(TeamColor color) {
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPiece piece = board.getPiece(new ChessPosition(i, j));
                if (piece != null &&
                        piece.getPieceType() == ChessPiece.PieceType.KING && piece.getTeamColor() == color) {
                    return new ChessPosition(i, j);
                }
            }
        }
        throw new RuntimeException("King position could not be found for team: " + color);
    }

    private Collection<ChessPosition> getAllPositionsOfTeam(TeamColor color) {
        Collection<ChessPosition> positions = new ArrayList<>();
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPiece piece = board.getPiece(new ChessPosition(i, j));
                if (piece != null && piece.getTeamColor() == color) {
                    positions.add(new ChessPosition(i, j));
                }
            }
        }
        return positions;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if (!isInCheck(teamColor)) { return false; }
        Collection<ChessPosition> teamPositions = getAllPositionsOfTeam(teamColor);
        for (ChessPosition pos : teamPositions) {
            Collection<ChessMove> validMoves = validMoves(pos);
            if (!validMoves.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (isInCheck(teamColor)) {
            return false;
        }
        Collection<ChessPosition> teamPositions = getAllPositionsOfTeam(teamColor);
        for (ChessPosition pos : teamPositions) {
            Collection<ChessMove> validMoves = validMoves(pos);
            if (!validMoves.isEmpty()) {
                return false;
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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return Objects.equals(board, chessGame.board) && teamTurn == chessGame.teamTurn;
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, teamTurn);
    }
}
