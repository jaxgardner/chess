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

    private ChessGame.TeamColor pieceColor;
    private ChessPiece.PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
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

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */

    private boolean isValidCoordinates(int row, int column){
        return row <= 8 && row >= 1 && column <=8 && column >=1;
    }

    private Collection<ChessMove> bishopMoves(ChessBoard board, ChessPosition myPosition){
        HashSet<ChessMove> moves = new HashSet<>();
        int myCurrentRow = myPosition.getRow();
        int myCurrentColumn = myPosition.getColumn();
        //Check forward-right diagonal
        while(myCurrentRow < 8 && myCurrentColumn < 8){
            myCurrentRow +=1;
            myCurrentColumn +=1;
            if(validateMoves(myCurrentRow, myCurrentColumn, board, myPosition, moves)) {
                break;
            }

        }

         myCurrentRow = myPosition.getRow();
         myCurrentColumn = myPosition.getColumn();

        //Check forward-left diagonal
        while(myCurrentRow > 1 && myCurrentColumn < 8){
            myCurrentRow--;
            myCurrentColumn ++;
            if(validateMoves(myCurrentRow, myCurrentColumn, board, myPosition, moves)) {
                break;
            }
        }

        myCurrentRow = myPosition.getRow();
        myCurrentColumn = myPosition.getColumn();
        //Check backward-right diagonal

        while(myCurrentRow < 8 && myCurrentColumn > 1){
            myCurrentRow++;
            myCurrentColumn--;
            if(validateMoves(myCurrentRow, myCurrentColumn, board, myPosition, moves)) {
                break;
            }
        }
        myCurrentRow = myPosition.getRow();
        myCurrentColumn = myPosition.getColumn();
        //Check backward-left diagonal
        while(myCurrentRow > 1  && myCurrentColumn > 1){
            myCurrentRow--;
            myCurrentColumn--;
            if(validateMoves(myCurrentRow, myCurrentColumn, board, myPosition, moves)) {
                break;
            }

        }
        return moves;
    }

    private Collection<ChessMove> kingMoves(ChessBoard board, ChessPosition myPosition){
        HashSet<ChessMove> moves = new HashSet<>();

        int[][] possibleCoordinates = {
                {myPosition.getRow() + 1, myPosition.getColumn()},
                {myPosition.getRow() + 1, myPosition.getColumn() + 1},
                {myPosition.getRow(), myPosition.getColumn() + 1},
                {myPosition.getRow() - 1, myPosition.getColumn() + 1},
                {myPosition.getRow() - 1, myPosition.getColumn()},
                {myPosition.getRow() - 1, myPosition.getColumn() - 1},
                {myPosition.getRow(), myPosition.getColumn() -1},
                {myPosition.getRow() + 1, myPosition.getColumn() - 1}
        };

        for(int i = 0; i < 8; i++){
            int[] current = possibleCoordinates[i];
            if(isValidCoordinates(current[0], current[1])){
                ChessPosition endLocation = new ChessPosition(current[0], current[1]);
                if(board.getPiece(endLocation) == null){
                    moves.add(new ChessMove(myPosition, endLocation, null));
                } else if (board.getPiece(endLocation).pieceColor != this.pieceColor){
                    moves.add(new ChessMove(myPosition, endLocation, null));
                }
            }
        }

        return moves;
    }

    private Collection<ChessMove> knightMoves(ChessBoard board, ChessPosition myPosition){
        HashSet<ChessMove> moves = new HashSet<>();

        int[][] possibleCoordinates = {
                {myPosition.getRow() + 2, myPosition.getColumn() + 1},
                {myPosition.getRow() + 2, myPosition.getColumn() - 1},
                {myPosition.getRow() - 2, myPosition.getColumn() + 1},
                {myPosition.getRow() - 2, myPosition.getColumn() - 1},
                {myPosition.getRow() + 1, myPosition.getColumn() + 2},
                {myPosition.getRow() + 1, myPosition.getColumn() - 2},
                {myPosition.getRow() - 1, myPosition.getColumn() + 2},
                {myPosition.getRow() - 1, myPosition.getColumn() - 2}
        };

        for(int i = 0; i < 8; i++){
            int[] current = possibleCoordinates[i];
            if(isValidCoordinates(current[0], current[1])){
                ChessPosition endLocation = new ChessPosition(current[0], current[1]);
                if(board.getPiece(endLocation) == null){
                    moves.add(new ChessMove(myPosition, endLocation, null));
                } else if (board.getPiece(endLocation).pieceColor != this.pieceColor){
                    moves.add(new ChessMove(myPosition, endLocation, null));
                }
            }
        }

        return moves;
    }

    private boolean validateMoves(int row, int column, ChessBoard board, ChessPosition myPosition, HashSet<ChessMove> moves){

        if(isValidCoordinates(row, column)){
            ChessPosition endPosition = new ChessPosition(row, column);

            if(board.getPiece(endPosition) == null){
                moves.add(new ChessMove(myPosition, endPosition, null));
            } else{
                if(board.getPiece(endPosition).pieceColor != this.pieceColor){
                    moves.add(new ChessMove(myPosition, endPosition, null));
                }
                return true;
            }
        }
        return false;
    }


    private Collection<ChessMove> rookMoves(ChessBoard board, ChessPosition myPosition){
        HashSet<ChessMove> moves = new HashSet<>();

        int row = myPosition.getRow();
        int column = myPosition.getColumn();

        while(row < 8){
            row++;
            if(validateMoves(row, column, board, myPosition, moves)) {
                break;
            }
        }

        row = myPosition.getRow();
        column = myPosition.getColumn();

        while(row > 1) {
            row--;
            if(validateMoves(row, column, board, myPosition, moves)) {
                break;
            }
        }

        row = myPosition.getRow();
        column = myPosition.getColumn();

        while(column < 8) {
            column++;
            if(validateMoves(row, column, board, myPosition, moves)) {
                break;
            }
        }

        row = myPosition.getRow();
        column = myPosition.getColumn();

        while(column > 1) {
            column--;
            if(validateMoves(row, column, board, myPosition, moves)) {
                break;
            }
        }


        return moves;
    }

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        if(this.type == PieceType.BISHOP){
            return bishopMoves(board, myPosition);
        } else if (this.type == PieceType.KING) {
            return kingMoves(board, myPosition);
        } else if (this.type == PieceType.KNIGHT) {
            return knightMoves(board, myPosition);
        } else if(this.type == PieceType.ROOK){
            return rookMoves(board, myPosition);
        }
        return null;
    }
}
