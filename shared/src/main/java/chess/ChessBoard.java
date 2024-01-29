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

    public ChessBoard(ChessBoard otherBoard) {
        this.board = new ChessPiece[8][8];

        for (int i = 0; i < otherBoard.board.length; i++) {
            for (int j = 0; j < otherBoard.board[i].length; j++) {
                ChessPosition tempP = new ChessPosition(i + 1, j + 1);
                if(otherBoard.getPiece(tempP) != null) {
                    this.board[i][j] = new ChessPiece(otherBoard.board[i][j]);
                }
                else{
                    this.board[i][j] = null;
                }
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessBoard that = (ChessBoard) o;
        return Arrays.deepEquals(board, that.board);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board);
    }

    @Override
    public String toString() {
        String boardString = "";
        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++){
                if(board[i][j] == null){
                    boardString += "- ";
                }
                else {
                    boardString += (board[i][j].toString() + " ");
                }
            }
            boardString += "\n";
        }
        return boardString;
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

    private void setPawns(ChessGame.TeamColor color){
        int row = 2;

        if(color == ChessGame.TeamColor.BLACK) {
            row = 7;
        }

        for(int i = 1; i <= 8; i++) {
            addPiece(new ChessPosition(row, i), new ChessPiece(color, ChessPiece.PieceType.PAWN));
        }

    }

    private void setPieces(ChessGame.TeamColor color){
        int row = 1;
        if(color == ChessGame.TeamColor.BLACK) {
            row = 8;
        }

        addPiece(new ChessPosition(row, 1), new ChessPiece(color, ChessPiece.PieceType.ROOK));
        addPiece(new ChessPosition(row, 2), new ChessPiece(color, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(row, 3), new ChessPiece(color, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(row, 4), new ChessPiece(color, ChessPiece.PieceType.QUEEN));
        addPiece(new ChessPosition(row, 5), new ChessPiece(color, ChessPiece.PieceType.KING));
        addPiece(new ChessPosition(row, 6), new ChessPiece(color, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(row, 7), new ChessPiece(color, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(row, 8), new ChessPiece(color, ChessPiece.PieceType.ROOK));

        setPawns(color);

    }


    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        board = new ChessPiece[8][8];
        setPieces(ChessGame.TeamColor.BLACK);
        setPieces(ChessGame.TeamColor.WHITE);
    }
}
