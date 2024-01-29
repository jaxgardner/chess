package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    private ChessGame.TeamColor pieceColor;
    private PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    public ChessPiece(ChessPiece other) {
        this.pieceColor = other.pieceColor;
        this.type = other.type;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }

    @Override
    public String toString() {
        String pieceString = "";
        if(type == PieceType.ROOK) {
            pieceString = "R";
        }
        else if(type == PieceType.KNIGHT) {
            pieceString = "N";
        }
        else if(type == PieceType.BISHOP) {
            pieceString = "B";
        }
        else if(type == PieceType.KING) {
            pieceString = "K";
        }
        else if(type == PieceType.QUEEN) {
            pieceString = "Q";
        }
        else if(type == PieceType.PAWN) {
            pieceString = "P";
        }

        return pieceColor == ChessGame.TeamColor.WHITE ? pieceString : pieceString.toLowerCase();

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

    private void verifyBasicMove(ChessBoard board, HashSet<ChessMove> moves, ChessPosition start, ChessPosition end) {
        if(board.getPiece(end) == null || board.getPiece(end).pieceColor != pieceColor) {
            moves.add(new ChessMove(start, end, null));
        }
    }

    private void verifyLongMoves(ChessBoard board, HashSet<ChessMove> moves, ChessPosition start, int rD, int cD) {
        int row = start.getRow() + rD;
        int col = start.getColumn() + cD;

        while(row <= 8 && row >= 1 && col <= 8 && col >= 1) {
            ChessPosition end = new ChessPosition(row, col);
            row += rD;
            col += cD;

            if(board.getPiece(end) == null) {
                moves.add(new ChessMove(start, end, null));
                continue;
            }
            else if(board.getPiece(end).pieceColor != pieceColor) {
                moves.add(new ChessMove(start, end, null));
            }
            break;
        }

    }


    private void getPromotionPieces(HashSet<ChessMove> moves, ChessPosition start, ChessPosition end) {
        moves.add(new ChessMove(start, end, PieceType.QUEEN));
        moves.add(new ChessMove(start, end, PieceType.BISHOP));
        moves.add(new ChessMove(start, end, PieceType.ROOK));
        moves.add(new ChessMove(start, end, PieceType.KNIGHT));
    }

    private void bishopMoves(ChessBoard board, HashSet<ChessMove> moves, ChessPosition start) {
        verifyLongMoves(board, moves, start, 1, 1);
        verifyLongMoves(board, moves, start, -1, -1);
        verifyLongMoves(board, moves, start, -1, 1);
        verifyLongMoves(board, moves, start, 1, -1);

    }

    private void rookMoves(ChessBoard board, HashSet<ChessMove> moves, ChessPosition start) {
        verifyLongMoves(board, moves, start, 1, 0);
        verifyLongMoves(board, moves, start, -1, 0);
        verifyLongMoves(board, moves, start, 0, 1);
        verifyLongMoves(board, moves, start, 0, -1);
    }

    private void queenMoves(ChessBoard board, HashSet<ChessMove> moves, ChessPosition start) {
        bishopMoves(board, moves, start);
        rookMoves(board, moves, start);
    }
    private void knightMoves(ChessBoard board, HashSet<ChessMove> moves, ChessPosition start){
        int row = start.getRow();
        int col = start.getColumn();

        int[][] possibleMoves = {
                {row + 2, col - 1},
                {row + 2, col + 1},
                {row - 2, col + 1},
                {row - 2, col - 1},
                {row - 1, col + 2},
                {row - 1, col - 2},
                {row + 1, col - 2 },
                {row + 1, col + 2},
        };

        for(int i = 0; i < 8; i++) {
            ChessPosition end = new ChessPosition(possibleMoves[i][0], possibleMoves[i][1]);
            if(end.getRow() >= 1 && end.getRow() <= 8 && end.getColumn() <= 8 && end.getColumn() >= 1) {
                verifyBasicMove(board, moves, start, end);
            }
        }
    }

    private void kingMoves(ChessBoard board, HashSet<ChessMove> moves, ChessPosition start){
        int row = start.getRow();
        int col = start.getColumn();

        int[][] possibleMoves = {
                {row + 1, col - 1},
                {row + 1, col},
                {row + 1, col + 1},
                {row - 1, col - 1},
                {row - 1, col},
                {row - 1, col + 1},
                {row, col -1 },
                {row, col + 1},
        };

        for(int i = 0; i < 8; i++) {
            ChessPosition end = new ChessPosition(possibleMoves[i][0], possibleMoves[i][1]);
            if(end.getRow() >= 1 && end.getRow() <= 8 && end.getColumn() <= 8 && end.getColumn() >= 1) {
                verifyBasicMove(board, moves, start, end);
            }
        }
    }

    private void pawnMoves(ChessBoard board, HashSet<ChessMove> moves, ChessPosition start) {
        int row = start.getRow();
        int col = start.getColumn();
        boolean teamWhite = pieceColor == ChessGame.TeamColor.WHITE;
        int[][] possibleMoves = new int[4][2];

        if(teamWhite) {
            possibleMoves[0] = new int[]{row + 1, col - 1};
            possibleMoves[1] = new int[]{row + 1, col };
            possibleMoves[2] = new int[]{row + 1, col + 1};
            possibleMoves[3] = new int[]{row + 2, col };
        }
        else {
            possibleMoves[0] = new int[]{row - 1, col - 1};
            possibleMoves[1] = new int[]{row - 1, col };
            possibleMoves[2] = new int[]{row - 1, col + 1};
            possibleMoves[3] = new int[]{row - 2, col };
        }

        for(int i = 0; i < 4; i++) {
            ChessPosition end = new ChessPosition(possibleMoves[i][0], possibleMoves[i][1]);
            if(!(end.getRow() <= 8 && end.getRow() >= 1 && end.getColumn() <= 8 && end.getColumn() >= 1)) {
                continue;
            }

            if(i == 0 || i == 2) {
                if(board.getPiece(end) != null && board.getPiece(end).pieceColor != pieceColor) {
                    if(teamWhite && end.getRow() == 8 || !(teamWhite) && end.getRow() == 1) {
                        getPromotionPieces(moves, start, end);
                    }
                    else {
                        moves.add(new ChessMove(start, end, null));
                    }
                }
            }

            else if(i == 1) {
                if(board.getPiece(end) == null) {
                    if(teamWhite && end.getRow() == 8 || !(teamWhite) && end.getRow() == 1) {
                        getPromotionPieces(moves, start, end);
                    }
                    else {
                        moves.add(new ChessMove(start, end, null));
                    }
                }
            }
            else {
                ChessPosition temp = teamWhite ? new ChessPosition(end.getRow() - 1, end.getColumn()) : new ChessPosition(end.getRow() + 1, end.getColumn());
                if(board.getPiece(end) == null && board.getPiece(temp) == null) {
                    if(teamWhite && row == 2 || !teamWhite && row == 7) {
                        moves.add(new ChessMove(start, end, null));
                    }
                }


            }


        }

    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        HashSet<ChessMove> moves = new HashSet<>();

        if(board.getPiece(myPosition).type == PieceType.KING) {
            kingMoves(board, moves, myPosition);
        }
        else if(board.getPiece(myPosition).type == PieceType.BISHOP) {
            bishopMoves(board, moves, myPosition);
        }
        else if(board.getPiece(myPosition).type == PieceType.ROOK) {
            rookMoves(board, moves, myPosition);
        }
        else if(board.getPiece(myPosition).type == PieceType.QUEEN) {
            queenMoves(board, moves, myPosition);
        }
        else if(board.getPiece(myPosition).type == PieceType.KNIGHT) {
            knightMoves(board, moves, myPosition);
        }
        else if(board.getPiece(myPosition).type == PieceType.PAWN) {
            pawnMoves(board, moves, myPosition);
        }

        return moves;
    }
}