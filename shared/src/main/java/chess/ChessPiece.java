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
            case BISHOP -> BishopMovesCalculator(board, myPosition, piece);
            case KING -> KingMovesCalculator(board, myPosition, piece);
            case KNIGHT -> KnightMovesCalculator(board, myPosition, piece);
            case PAWN -> PawnMovesCalculator(board, myPosition, piece);
            case QUEEN -> QueenMovesCalculator(board, myPosition, piece);
            case ROOK -> RookMovesCalculator(board, myPosition, piece);
        };
    }

    private Collection<ChessMove> BishopMovesCalculator(ChessBoard board, ChessPosition myPosition, ChessPiece piece) {
        int [][] directions = {{-1,-1},{-1,1},{1,-1},{1,1}};
        List<ChessMove> possibilities = new ArrayList<>();

        for (int[] direction: directions) {
            int row = myPosition.getRow() + direction[0];
            int col = myPosition.getColumn() + direction[1];

            while (row>0 && row <9 && col>0 && col <9) {
                ChessPosition destination = new ChessPosition(row,col);
                ChessPiece target = board.getPiece(destination);

                if (target == null) {
                    possibilities.add(new ChessMove(myPosition, destination, null));
                }
                else {
                    if (target.getTeamColor() != piece.getTeamColor()) {
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

    private Collection<ChessMove> KingMovesCalculator(ChessBoard board, ChessPosition myPosition, ChessPiece piece) {
        int [][] directions = {{-1,-1},{-1,0},{-1,1},{0,1},{-1,1},{1,1},{1,0},{1,-1}};
        List<ChessMove> possibilities = new ArrayList<>();

        for (int[] direction: directions) {
            int row = myPosition.getRow() + direction[0];
            int col = myPosition.getColumn() + direction[1];

            if (row>0 && row <9 && col>0 && col <9) {
                ChessPosition destination = new ChessPosition(row,col);
                ChessPiece target = board.getPiece(destination);

                if (target == null) {
                    possibilities.add(new ChessMove(myPosition, destination, null));
                } else if (target.getTeamColor() != piece.getTeamColor()) {
                    possibilities.add(new ChessMove(myPosition, destination, null));
                }
            }
        }
        return possibilities;
    }

    private Collection<ChessMove> KnightMovesCalculator(ChessBoard board, ChessPosition myPosition, ChessPiece piece) {
        int [][] directions = {{-1,2},{1,2},{2,1},{2,-1},{-1,-2},{1,-2},{-2,-1},{-2,1}};
        List<ChessMove> possibilities = new ArrayList<>();

        for (int[] direction: directions) {
            int row = myPosition.getRow() + direction[0];
            int col = myPosition.getColumn() + direction[1];

            if (row>0 && row <9 && col>0 && col <9) {
                ChessPosition destination = new ChessPosition(row,col);
                ChessPiece target = board.getPiece(destination);

                if (target == null) {
                    possibilities.add(new ChessMove(myPosition, destination, null));
                } else if (target.getTeamColor() != piece.getTeamColor()) {
                    possibilities.add(new ChessMove(myPosition, destination, null));
                }
            }
        }
        return possibilities;
    }

    private Collection<ChessMove> PawnMovesCalculator(ChessBoard board, ChessPosition myPosition, ChessPiece piece) {
        return List.of(new ChessMove(new ChessPosition(5,4), new ChessPosition(1,8), null));
    }

    private Collection<ChessMove> QueenMovesCalculator(ChessBoard board, ChessPosition myPosition, ChessPiece piece) {
        return List.of(new ChessMove(new ChessPosition(5,4), new ChessPosition(1,8), null));
    }

    private Collection<ChessMove> RookMovesCalculator(ChessBoard board, ChessPosition myPosition, ChessPiece piece) {
        return List.of(new ChessMove(new ChessPosition(5,4), new ChessPosition(1,8), null));
    }
}
