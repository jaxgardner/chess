package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import static ui.EscapeSequences.*;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

public class BoardPrinter {
    private static final int BOARD_SIZE = 10;
    private static final String LINE_SPACE = " ";
    private final PrintStream out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
    private static final String COORDINATES_WHITE = " ABCDEFGH ";
    private static final String COORDINATES_BLACK = " ABCDEFGH ";
    private static final String NUMBERS_BLACK = "12345678";
    private static final String NUMBERS_WHITE = "87654321";
    private ChessBoard board;


    public BoardPrinter() {
        board = new ChessBoard();
        board.resetBoard();
    }

    public void setBoard(ChessBoard board) {
        this.board = new ChessBoard(board);
    }

    public void printChessBoard(String teamColor) {
         String coords;
         String nums;

         if(teamColor.equalsIgnoreCase("white")) {
             coords = COORDINATES_WHITE;
             nums = NUMBERS_WHITE;
         }
         else {
             coords = COORDINATES_BLACK;
             nums = NUMBERS_BLACK;
         }

         printLetters(coords);
         printCheckers(nums);
         printLetters(coords);
     }

    private void printCheckers(String nums) {
        for (int i = 0; i < 8; i++) {
            out.print(SET_BG_COLOR_DARK_GREY + LINE_SPACE + nums.charAt(i) + LINE_SPACE + "\u001B[0m");
            for (int j = 0; j < 8; j++) {
                ChessPosition position = getPosition(nums, i + 1, j + 1);
                printPiece(position);
            }
            out.print(SET_BG_COLOR_DARK_GREY + LINE_SPACE + nums.charAt(i) + LINE_SPACE + "\u001B[0m");
            out.println();
        }
    }


    private void printLetters(String coordinates) {
        for (int i = 0; i < BOARD_SIZE; ++i) {
            out.print(SET_BG_COLOR_DARK_GREY + LINE_SPACE + coordinates.charAt(i) + LINE_SPACE + "\u001B[0m");
        }
        out.println();
    }

    private void printPiece(ChessPosition position) {
        ChessPiece piece = board.getPiece(position);

        if(piece != null) {
            out.print(getBGColor(position.getRow(), position.getColumn())  + getPieceColor(piece) + LINE_SPACE + piece + LINE_SPACE + "\u001B[0m");
        }
        else {
            out.print(getBGColor(position.getRow(), position.getColumn())  + LINE_SPACE.repeat(3) + "\u001B[0m");
        }
    }

    private String getPieceColor(ChessPiece piece) {
        if(piece.getTeamColor() == ChessGame.TeamColor.BLACK) {
            return SET_TEXT_COLOR_GREEN;
        }
        return SET_TEXT_COLOR_BLUE;
    }

    private String getBGColor(int i, int j) {
        if((i + j) % 2 == 0) {
            return SET_BG_COLOR_LIGHT_GREY;
        }
        return SET_BG_COLOR_BLACK;
    }

    private ChessPosition getPosition(String nums, int i , int j) {
       if(nums.equals(NUMBERS_WHITE)) {
            i = 9 - i;
            return new ChessPosition(i, j);
       }
       return new ChessPosition(i, j);
    }
}
