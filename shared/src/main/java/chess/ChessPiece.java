package chess;

import java.util.ArrayList;
import java.util.Collection;

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

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */

    private Collection<ChessMove> bishopMoves(ChessBoard board, ChessPosition myPosition){
        ArrayList<ChessMove> moves = new ArrayList<>();
        int myCurrentRow = myPosition.getRow();
        int myCurrentColumn = myPosition.getColumn();
        //Check forward-right diagonal
        while(myCurrentRow < 8 && myCurrentColumn < 8){
            myCurrentRow +=1;
            myCurrentColumn +=1;
            ChessPiece positionPiece = board.getPiece(new ChessPosition(myCurrentRow , myCurrentColumn ));

            if(positionPiece != null){
                if(positionPiece.getTeamColor() == this.pieceColor){
                    continue;
                }
            }
            else {
                moves.add(new ChessMove(myPosition, new ChessPosition(myCurrentRow , myCurrentColumn ), null));
            }

        }

         myCurrentRow = myPosition.getRow();
         myCurrentColumn = myPosition.getColumn();

        //Check forward-left diagonal
        while(myCurrentRow > 1 && myCurrentColumn < 8){
            myCurrentRow--;
            myCurrentColumn ++;
            ChessPiece positionPiece = board.getPiece(new ChessPosition(myCurrentRow , myCurrentColumn ));

            if(positionPiece != null){
                if(positionPiece.getTeamColor() == this.pieceColor){
                    continue;
                }
            }
            else {
                moves.add(new ChessMove(myPosition, new ChessPosition(myCurrentRow , myCurrentColumn ), null));
            }

        }

        myCurrentRow = myPosition.getRow();
        myCurrentColumn = myPosition.getColumn();
        //Check backward-right diagonal

        while(myCurrentRow < 8 && myCurrentColumn > 1){
            myCurrentRow++;
            myCurrentColumn--;
            ChessPiece positionPiece = board.getPiece(new ChessPosition(myCurrentRow , myCurrentColumn ));

            if(positionPiece != null){
                if(positionPiece.getTeamColor() == this.pieceColor){
                    continue;
                }
            }
            else {
                moves.add(new ChessMove(myPosition, new ChessPosition(myCurrentRow , myCurrentColumn ), null));
            }

        }
        myCurrentRow = myPosition.getRow();
        myCurrentColumn = myPosition.getColumn();
        //Check backward-left diagonal
        while(myCurrentRow > 1  && myCurrentColumn > 1){
            myCurrentRow--;
            myCurrentColumn--;
            ChessPiece positionPiece = board.getPiece(new ChessPosition(myCurrentRow , myCurrentColumn ));

            if(positionPiece != null){
                if(positionPiece.getTeamColor() == this.pieceColor){
                    continue;
                }
            }
            else {
                moves.add(new ChessMove(myPosition, new ChessPosition(myCurrentRow , myCurrentColumn ), null));
            }

        }

        return moves;
    }

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        return bishopMoves(board, myPosition);
    }
}
