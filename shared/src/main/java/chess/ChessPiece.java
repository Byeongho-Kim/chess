package chess;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.ArrayList;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;
    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(pieceColor);
        result = 31 * result + Objects.hashCode(type);
        return result;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece piece = board.getPiece(myPosition);
        return switch (piece.getPieceType()) {
            case BISHOP -> bishopMovesCalculator(board, myPosition, piece);
            case KING -> kingMovesCalculator(board, myPosition, piece);
            case KNIGHT -> knightMovesCalculator(board, myPosition, piece);
            case PAWN -> pawnMovesCalculator(board, myPosition, piece);
            case QUEEN -> queenMovesCalculator(board, myPosition, piece);
            case ROOK -> rookMovesCalculator(board, myPosition, piece);
        };
    }

    private Collection<ChessMove> bishopMovesCalculator(ChessBoard board, ChessPosition myPosition, ChessPiece piece) {
        int [][] directions = {{-1,-1},{-1,1},{1,-1},{1,1}};
        return calculateMoves(board, myPosition, directions, 8);
    }

    private Collection<ChessMove> kingMovesCalculator(ChessBoard board, ChessPosition myPosition, ChessPiece piece) {
        int [][] directions = {{-1,1},{0,1},{1,1},{1,0},{1,-1},{0,-1},{-1,-1},{-1,0}};
        return calculateMoves(board, myPosition, directions, 1);
    }

    private Collection<ChessMove> knightMovesCalculator(ChessBoard board, ChessPosition myPosition, ChessPiece piece) {
        int [][] directions = {{-1,2},{1,2},{2,1},{2,-1},{-1,-2},{1,-2},{-2,-1},{-2,1}};
        return calculateMoves(board, myPosition, directions, 1);
    }

    private Collection<ChessMove> pawnMovesCalculator(ChessBoard board, ChessPosition myPosition, ChessPiece piece) {
        List<ChessMove> possibilities = new ArrayList<>();
        PawnMovementParams params = getPawnMovementParams(piece);
        addForwardMoves(possibilities, board, myPosition, params);
        addCaptureMoves(possibilities, board, myPosition, params);

        return possibilities;
    }

    private PawnMovementParams getPawnMovementParams(ChessPiece piece) {
        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            return new PawnMovementParams(2, 1, 8);
        }
        else {
            return new PawnMovementParams(7, -1, 1);
        }
    }

    private void addForwardMoves(List<ChessMove> possibilities, ChessBoard board,
                                 ChessPosition myPosition, PawnMovementParams params) {
        int row = myPosition.getRow() + params.direction;

        if (row > 0 && row < 9) {
            ChessPosition destination = new ChessPosition(row, myPosition.getColumn());
            ChessPiece target = board.getPiece(destination);

            if (target == null) {
                addPromotionMoves(possibilities, myPosition, destination, row, params.promotionPoint);

                if (myPosition.getRow() == params.startingPoint) {
                    addDoubleMove(possibilities, board, myPosition, params);
                }
            }
        }
    }

    private void addPromotionMoves(List<ChessMove> possibilities, ChessPosition myPosition,
                                   ChessPosition destination, int row, int promotionPoint) {
        if (row == promotionPoint) {
            possibilities.add(new ChessMove(myPosition, destination, PieceType.BISHOP));
            possibilities.add(new ChessMove(myPosition, destination, PieceType.KNIGHT));
            possibilities.add(new ChessMove(myPosition, destination, PieceType.QUEEN));
            possibilities.add(new ChessMove(myPosition, destination, PieceType.ROOK));
        }
        else {
            possibilities.add(new ChessMove(myPosition, destination, null));
        }
    }

    private void addDoubleMove(List<ChessMove> possibilities, ChessBoard board,
                               ChessPosition myPosition, PawnMovementParams params) {
        int twicePoint = myPosition.getRow() + (params.direction * 2);

        if (twicePoint > 0 && twicePoint < 9) {
            ChessPosition twiceDestination = new ChessPosition(twicePoint, myPosition.getColumn());
            ChessPiece twiceTarget = board.getPiece(twiceDestination);

            if (twiceTarget == null) {
                possibilities.add(new ChessMove(myPosition, twiceDestination, null));
            }
        }
    }

    private void addCaptureMoves(List<ChessMove> possibilities, ChessBoard board,
                                 ChessPosition myPosition, PawnMovementParams params) {
        int row = myPosition.getRow() + params.direction;
        int[] captureCol = {myPosition.getColumn() - 1, myPosition.getColumn() + 1};

        for (int col : captureCol) {
            if (col > 0 && col < 9) {
                ChessPosition captureDestination = new ChessPosition(row, col);
                ChessPiece captureTarget = board.getPiece(captureDestination);

                if (captureTarget != null && captureTarget.getTeamColor() != getTeamColor()) {
                    addPromotionMoves(possibilities, myPosition, captureDestination, row, params.promotionPoint);
                }
            }
        }
    }

    private static class PawnMovementParams {
        final int startingPoint;
        final int direction;
        final int promotionPoint;

        PawnMovementParams(int startingPoint, int direction, int promotionPoint) {
            this.startingPoint = startingPoint;
            this.direction = direction;
            this.promotionPoint = promotionPoint;
        }
    }

    private Collection<ChessMove> queenMovesCalculator(ChessBoard board, ChessPosition myPosition, ChessPiece piece) {
        int [][] directions = {{-1,1},{0,1},{1,1},{1,0},{1,-1},{0,-1},{-1,-1},{-1,0}};
        return calculateMoves(board, myPosition, directions, 8);
    }

    private Collection<ChessMove> rookMovesCalculator(ChessBoard board, ChessPosition myPosition, ChessPiece piece) {
        int [][] directions = {{0,1},{1,0},{0,-1},{-1,0}};
        return calculateMoves(board, myPosition, directions, 8);
    }

    private Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition,
                                                 int[][] directions, int maxDistance) {
        List<ChessMove> possibilities = new ArrayList<>();

        for (int[] direction : directions) {
            int row = myPosition.getRow() + direction[0];
            int col = myPosition.getColumn() + direction[1];

            for (int i = 0; i < maxDistance; i++) {
                if (row < 1 || row > 8 || col < 1 || col > 8) {
                    break;
                }

                ChessPosition destination = new ChessPosition(row, col);
                ChessPiece target = board.getPiece(destination);

                if (target == null) {
                    possibilities.add(new ChessMove(myPosition, destination, null));
                }
                else {
                    if (target.getTeamColor() != getTeamColor()) {
                        possibilities.add(new ChessMove(myPosition, destination, null));
                    }
                    break;
                }
                row += direction[0];
                col += direction[1];
            }
        }
        return possibilities;
    }
}
