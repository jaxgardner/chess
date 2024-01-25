package chess;

import java.util.HashSet;
import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    private ChessGame.TeamColor teamColor;
    private PieceType pieceType;

    public ChessPiece(ChessGame.TeamColor teamColor, PieceType pieceType) {
        this.teamColor = teamColor;
        this.pieceType = pieceType;
    }

    @Override
    public String toString() {
        String pieceString = "";
        if (pieceType == PieceType.KING) {
            pieceString = "k";
        } else if (pieceType == PieceType.QUEEN) {
            pieceString = "q";
        } else if (pieceType == PieceType.KNIGHT) {
            pieceString = "n";
        } else if (pieceType == PieceType.BISHOP) {
            pieceString = "b";
        } else if (pieceType == PieceType.ROOK) {
            pieceString = "r";
        } else if (pieceType == PieceType.PAWN) {
            pieceString = "p";
        }

        if (teamColor == ChessGame.TeamColor.WHITE) {
            pieceString = pieceString.toUpperCase();
        }

        return pieceString;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiece that = (ChessPiece) o;
        return teamColor == that.teamColor && pieceType == that.pieceType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamColor, pieceType);
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
        return teamColor;
    }


    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return pieceType;
    }

    private void verifyBasicMove(ChessBoard board, HashSet<ChessMove> moves, ChessPosition start, ChessPosition end) {
        if(board.getPiece(end) == null || board.getPiece(end).getTeamColor() != teamColor) {
            moves.add(new ChessMove(start, end, null));
        }
    }

    private void verifyLongMove(ChessBoard board, HashSet<ChessMove> moves, ChessPosition start, int rD, int cD) {
        int row = start.getRow();
        int col = start.getColumn();
        row += rD;
        col += cD;

        while(row <= 8 && row >= 1 && col <= 8 && col >= 1) {
            ChessPosition end = new ChessPosition(row, col);

            if(board.getPiece(end) == null) {
                moves.add(new ChessMove(start, end, null));
                row += rD;
                col += cD;
                continue;
            }
            else if(board.getPiece(end).teamColor != teamColor) {
                moves.add(new ChessMove(start, end, null));
            }
            break;
        }
    }

    private void getPromotionPieces(ChessPosition start, ChessPosition end, HashSet<ChessMove> moves) {
        moves.add(new ChessMove(start, end, PieceType.BISHOP));
        moves.add(new ChessMove(start, end, PieceType.ROOK));
        moves.add(new ChessMove(start, end, PieceType.KNIGHT));
        moves.add(new ChessMove(start, end, PieceType.QUEEN));
    }

    private void pawnMoves(ChessBoard board, ChessPosition start, HashSet<ChessMove> moves) {
        int row = start.getRow();
        int col = start.getColumn();
        boolean teamWhite = (teamColor == ChessGame.TeamColor.WHITE);

        int[][] possibleMoves = new int[4][2];

        if(teamWhite) {
            possibleMoves[0] = new int []{row + 1, col - 1};
            possibleMoves[1] = new int []{row + 1, col};
            possibleMoves[2] = new int []{row + 1, col + 1};
            possibleMoves[3] = new int []{row + 2, col};
        }
        else {
            possibleMoves[0] = new int []{row - 1, col - 1};
            possibleMoves[1] = new int []{row - 1, col};
            possibleMoves[2] = new int []{row - 1, col + 1};
            possibleMoves[3] = new int []{row - 2, col};
        }


        for(int i = 0; i < 4; i++) {
            ChessPosition end = new ChessPosition(possibleMoves[i][0], possibleMoves[i][1]);
            if(!(end.getRow() <= 8 && end.getRow() >= 1 && end.getColumn() <= 8 && end.getColumn() >= 1)) {
                continue;
            }
            if(i == 0 || i == 2) {
                if(board.getPiece(end) != null && board.getPiece(end).teamColor != teamColor) {
                    if(teamWhite && end.getRow() == 8 || !(teamWhite) && end.getRow() == 1) {
                        getPromotionPieces(start, end, moves);
                    }
                    else {
                        moves.add(new ChessMove(start, end, null));
                    }
                }
            }
            else if(i == 1) {
                if(board.getPiece(end) == null) {
                    if(teamWhite && end.getRow() == 8 || !(teamWhite) && end.getRow() == 1) {
                        getPromotionPieces(start, end, moves);
                    }
                    else {
                        moves.add(new ChessMove(start, end, null));
                    }
                }
            }
            else {
                if(teamWhite && row == 2 || !(teamWhite) && row == 7) {
                    ChessPosition temp;
                   if(teamWhite){
                       temp = new ChessPosition(end.getRow() - 1, end.getColumn());
                   } else {
                       temp = new ChessPosition(end.getRow() + 1, end.getColumn());
                   }
                    if(board.getPiece(end) == null && board.getPiece(temp) == null ){
                        moves.add(new ChessMove(start, end, null));
                    }
                }
            }
        }
    }


    private void rookMoves(ChessBoard board, ChessPosition start, HashSet<ChessMove> moves) {
        verifyLongMove(board, moves, start, 1, 0);
        verifyLongMove(board, moves, start, -1, 0);
        verifyLongMove(board, moves, start, 0, 1);
        verifyLongMove(board, moves, start, 0, -1);
    }

    private void bishopMoves(ChessBoard board, ChessPosition start, HashSet<ChessMove> moves) {
        verifyLongMove(board, moves, start, 1, 1);
        verifyLongMove(board, moves, start, 1, -1);
        verifyLongMove(board, moves, start, -1, 1);
        verifyLongMove(board, moves, start, -1, -1);
    }

    private void queenMoves(ChessBoard board, ChessPosition start, HashSet<ChessMove> moves) {
        rookMoves(board, start, moves);
        bishopMoves(board, start, moves);
    }

    private void knightMoves(ChessBoard board, ChessPosition start, HashSet<ChessMove> moves) {
        int row = start.getRow();
        int col = start.getColumn();

        int[][] possibleMoves = {
                {row + 2, col + 1},
                {row + 2, col - 1},
                {row + 1, col + 2},
                {row + 1, col - 2},
                {row - 2, col - 1},
                {row - 2, col + 1},
                {row - 1, col - 2},
                {row- 1, col + 2},
        };

        for(int i = 0; i < 8; i++) {
            ChessPosition end = new ChessPosition(possibleMoves[i][0], possibleMoves[i][1]);
            if(end.getRow() <= 8 && end.getRow() >= 1 && end.getColumn() <= 8 && end.getColumn() >= 1){
                verifyBasicMove(board, moves, start, end);
            }
        }

    }

    private void kingMoves (ChessBoard board, ChessPosition start, HashSet<ChessMove> moves){
        int row = start.getRow();
        int col = start.getColumn();

        int[][] possibleMoves = {
                {row + 1, col},
                {row + 1, col + 1},
                {row + 1, col - 1},
                {row, col + 1},
                {row, col - 1},
                {row - 1, col},
                {row - 1, col + 1},
                {row- 1, col - 1},
        };

        for(int i = 0; i < 8; i++) {
            ChessPosition end = new ChessPosition(possibleMoves[i][0], possibleMoves[i][1]);
            if(end.getRow() <= 8 && end.getRow() >= 1 && end.getColumn() <= 8 && end.getColumn() >= 1){
                verifyBasicMove(board, moves, start, end);
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
        if(board.getPiece(myPosition).pieceType == PieceType.KING){
            kingMoves(board, myPosition, moves);
        }
        else if(board.getPiece(myPosition).pieceType == PieceType.KNIGHT){
            knightMoves(board, myPosition, moves);
        }
        else if(board.getPiece(myPosition).pieceType == PieceType.ROOK) {
            rookMoves(board, myPosition, moves);
        }
        else if(board.getPiece(myPosition).pieceType == PieceType.BISHOP) {
            bishopMoves(board, myPosition, moves);
        }
        else if(board.getPiece(myPosition).pieceType == PieceType.QUEEN) {
            queenMoves(board, myPosition, moves);
        }
        else if(board.getPiece(myPosition).pieceType == PieceType.PAWN) {
            pawnMoves(board, myPosition, moves);
        }

        return moves;

    }

}
