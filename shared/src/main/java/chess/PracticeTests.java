package chess;

import java.util.HashSet;

public class PracticeTests {
    public static void main(String[] args) throws InvalidMoveException {
        ChessBoard board = new ChessBoard();
        board.resetBoard();
        //System.out.println(board.toString());
        ChessGame chessGame = new ChessGame();
        chessGame.setBoard(board);


        ChessPosition start = new ChessPosition(2, 4);
        ChessPosition end = new ChessPosition(3, 4);

        chessGame.findKing();
    }

}
