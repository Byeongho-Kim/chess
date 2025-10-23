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

    private Collection<ChessMove> bishopMovesCalculator(ChessBoard board, ChessPosition myPosition, ChessPiece piece) {
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

    private Collection<ChessMove> kingMovesCalculator(ChessBoard board, ChessPosition myPosition, ChessPiece piece) {
        int [][] directions = {{-1,1},{0,1},{1,1},{1,0},{1,-1},{0,-1},{-1,-1},{-1,0}};
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

    private Collection<ChessMove> knightMovesCalculator(ChessBoard board, ChessPosition myPosition, ChessPiece piece) {
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

    private Collection<ChessMove> pawnMovesCalculator(ChessBoard board, ChessPosition myPosition, ChessPiece piece) {
        int startingPoint;
        int direction;
        int promotionPoint;
        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
             startingPoint= 2;
             direction = 1;
             promotionPoint = 8;
         }
         else {
             startingPoint= 7;
             direction = -1;
            promotionPoint = 1;
         }
        List<ChessMove> possibilities = new ArrayList<>();
        int row = myPosition.getRow() + direction;

        if (row>0 && row <9) {
            ChessPosition destination = new ChessPosition(row, myPosition.getColumn());
            ChessPiece target = board.getPiece(destination);

            if (target == null) {
                if (row == promotionPoint) {
                    possibilities.add(new ChessMove(myPosition, destination, PieceType.BISHOP));
                    possibilities.add(new ChessMove(myPosition, destination, PieceType.KNIGHT));
                    possibilities.add(new ChessMove(myPosition, destination, PieceType.QUEEN));
                    possibilities.add(new ChessMove(myPosition, destination, PieceType.ROOK));
                }
                else
                    possibilities.add(new ChessMove(myPosition, destination, null));

                if (myPosition.getRow() == startingPoint) {
                    int twicePoint = myPosition.getRow() + (direction * 2);
                    if (twicePoint > 0 && twicePoint < 9) {
                        ChessPosition twiceDestination = new ChessPosition(twicePoint, myPosition.getColumn());
                        ChessPiece twiceTarget = board.getPiece(twiceDestination);
                        if (twiceTarget == null) {
                            possibilities.add(new ChessMove(myPosition, twiceDestination, null));
                        }
                    }
                }
            }
        }
        int[] captureCol = {myPosition.getColumn()-1, myPosition.getColumn()+1};
        for (int col : captureCol) {
            if (col > 0 && col < 9) {
                ChessPosition captureDestination = new ChessPosition(row,col);
                ChessPiece captureTarget = board.getPiece(captureDestination);
                if (captureTarget != null && captureTarget.getTeamColor() != piece.getTeamColor()) {
                    if (row == promotionPoint) {
                        possibilities.add(new ChessMove(myPosition, captureDestination, PieceType.BISHOP));
                        possibilities.add(new ChessMove(myPosition, captureDestination, PieceType.KNIGHT));
                        possibilities.add(new ChessMove(myPosition, captureDestination, PieceType.QUEEN));
                        possibilities.add(new ChessMove(myPosition, captureDestination, PieceType.ROOK));
                    }
                    else
                        possibilities.add(new ChessMove(myPosition, captureDestination, null));
                }
            }
        }
        return possibilities;
    }

    private Collection<ChessMove> queenMovesCalculator(ChessBoard board, ChessPosition myPosition, ChessPiece piece) {
        int [][] directions = {{-1,1},{0,1},{1,1},{1,0},{1,-1},{0,-1},{-1,-1},{-1,0}};
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

    private Collection<ChessMove> rookMovesCalculator(ChessBoard board, ChessPosition myPosition, ChessPiece piece) {
        int [][] directions = {{0,1},{1,0},{0,-1},{-1,0}};
        List<ChessMove> possibilities = new ArrayList<>();

        for (int[] direction: directions) {
            int row = myPosition.getRow() + direction[0];
            int col = myPosition.getColumn() + direction[1];

            while (row > 0 && row < 9 && col > 0 && col < 9) {
                ChessPosition destination = new ChessPosition(row, col);
                ChessPiece target = board.getPiece(destination);

                if (target == null) {
                    possibilities.add(new ChessMove(myPosition, destination, null));
                } else {
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
}
