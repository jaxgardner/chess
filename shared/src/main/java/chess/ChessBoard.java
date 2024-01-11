package chess;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    private ChessPiece[][] board;

    public ChessBoard() {
        board = new ChessPiece[8][8];
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        board[position.getRow() - 1][position.getColumn() - 1] = piece;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessBoard that = (ChessBoard) o;
        return Arrays.equals(board, that.board);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(board);
    }

    @Override
    public String toString(){
        String out = "";
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                out = out + board[i][j] + " ";
            }
            out += "\n";
        }
        return out;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return board[position.getRow() - 1][position.getColumn() - 1];
    }

    private void setPawns(int row, ChessGame.TeamColor team){
        for(int i = 1; i < 9; i++){
            addPiece(new ChessPosition(row, i), new ChessPiece(team, ChessPiece.PieceType.PAWN));
        }
    }

    private void setPieces(ChessGame.TeamColor team) {
        int row;
        if(team == ChessGame.TeamColor.WHITE){
            row = 1;
        } else {
            row = 8;
        }
        addPiece(new ChessPosition(row, 1), new ChessPiece(team, ChessPiece.PieceType.ROOK));
        addPiece(new ChessPosition(row, 2), new ChessPiece(team, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(row, 3), new ChessPiece(team, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(row, 4), new ChessPiece(team, ChessPiece.PieceType.QUEEN));
        addPiece(new ChessPosition(row, 5), new ChessPiece(team, ChessPiece.PieceType.KING));
        addPiece(new ChessPosition(row, 6), new ChessPiece(team, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(row, 7), new ChessPiece(team, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(row, 8), new ChessPiece(team, ChessPiece.PieceType.ROOK));

        if(team == ChessGame.TeamColor.WHITE){
            row = 2;
        } else {
            row = 7;
        }
        setPawns(row, team);
    }
    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        //clear board
        board = new ChessPiece[8][8];

        //Set up black side
        setPieces(ChessGame.TeamColor.BLACK);

        //Set up white side
        setPieces(ChessGame.TeamColor.WHITE);
    }
}
