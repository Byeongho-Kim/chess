package chess;

import java.util.ArrayList;
import java.util.List;
import java.util.Collection;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private TeamColor teamTurn;
    private ChessBoard board;

    public ChessGame() {
        this.teamTurn = TeamColor.WHITE;
        this.board = new ChessBoard();
        this.board.resetBoard();
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
        List<ChessMove> validMoves = new ArrayList<>();

        ChessPiece piece = board.getPiece(startPosition);
        if (piece == null)
            return null;

        Collection<ChessMove> possibilities = piece.pieceMoves(board, startPosition);

        for (ChessMove possibleMove : possibilities) {
            if (isValid(possibleMove))
                validMoves.add(possibleMove);
        }

        return validMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece piece = board.getPiece(move.getStartPosition());

        if (piece == null)
            throw new InvalidMoveException();
        if (piece.getTeamColor() != teamTurn)
            throw new InvalidMoveException();
        if (!isValid(move))
            throw new InvalidMoveException();

        ChessPiece newPiece = board.getPiece(move.getStartPosition());
        board.addPiece(move.getStartPosition(), null);

        if (move.getPromotionPiece() != null)
            board.addPiece(move.getEndPosition(), new ChessPiece(newPiece.getTeamColor(), move.getPromotionPiece()));
        else
            board.addPiece(move.getEndPosition(), newPiece);

        teamTurn = (teamTurn == TeamColor.BLACK) ? TeamColor.WHITE : TeamColor.BLACK;
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {

        ChessPosition king = KingPosition(teamColor);
        if (king == null)
            return false;

        TeamColor opposingColor = (teamColor == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
        for (int row = 1; row <9; row++) {
            for (int col = 1; col <9; col++) {
                ChessPosition opposingPosition = new ChessPosition(row, col);
                ChessPiece opposingPiece = board.getPiece(opposingPosition);

                if (opposingPiece != null && opposingPiece.getTeamColor() == opposingColor) {
                    Collection<ChessMove> opposingMoves = opposingPiece.pieceMoves(board, opposingPosition);

                    for (ChessMove opposingMove: opposingMoves) {
                        if (opposingMove.getEndPosition() == king)
                            return true;
                    }
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
        if (!isInCheck(teamColor))
            return false;

        for (int row = 1; row <9; row++) {
            for (int col = 1; col <9; col++) {
                ChessPosition kingPosition = new ChessPosition(row, col);
                ChessPiece opposingPiece = board.getPiece(opposingPosition);
            }
        }
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
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

    private ChessPosition KingPosition(TeamColor teamColor) {
        for (int row = 1; row < 9; row ++) {
            for (int col = 1; col < 9; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);

                if (piece != null && piece.getPieceType() == ChessPiece.PieceType.KING && piece.getTeamColor() == teamColor)
                    return position;
            }
        }
        return null;
    }

    private boolean isValid (ChessMove move) {
        ChessPiece originalStartPiece = board.getPiece(move.getStartPosition());
        ChessPiece originalEndPiece = board.getPiece(move.getEndPosition());

        try {
            board.addPiece(move.getStartPosition(), null);

            if (move.getPromotionPiece() != null )
                board.addPiece(move.getEndPosition(), new ChessPiece(originalStartPiece.getTeamColor(), move.getPromotionPiece()));
            else
                board.addPiece(move.getEndPosition(), originalStartPiece);

            boolean kingInCheck = isInCheck(originalStartPiece.getTeamColor());

            return !kingInCheck;
        }
        finally {
            board.addPiece(move.getStartPosition(), originalStartPiece);
            board.addPiece(move.getEndPosition(), originalEndPiece);
        }
    }
}
