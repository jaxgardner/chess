package ui;

import static ui.EscapeSequences.*;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

public class BoardPrinter {
    private static final int BOARD_SIZE = 10;
    private static final int LINE_WIDTH = 1;
    private final PrintStream out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

    private static String coordinates = " abcdefgh ";
    private static String coordinatesNumbers = "123445678";
    private static String coordinatesReversed = "  h  g  f  e  d  c  b  a  ";


     public void printChessBoard() {

         for(int i = 0; i < 10; ++i) {
             out.print(SET_BG_COLOR_DARK_GREY + " " +  coordinates.charAt(i) + " " + "\u001B[0m");
         }

         out.println();

         for (int i = 0; i <= 8; i++) {
             out.print(SET_BG_COLOR_DARK_GREY + " " + coordinatesNumbers.charAt(i) + " " + "\u001B[0m");
             for (int j = 0; j < 8; j++) {
                 if ((i + j) % 2 == 0) {
                     out.print(SET_BG_COLOR_BLACK  + "   " + "\u001B[0m");
                 } else {
                     out.print(SET_BG_COLOR_LIGHT_GREY  + "   " + "\u001B[0m");
                 }
             }
             out.print(SET_BG_COLOR_DARK_GREY + " " + coordinatesNumbers.charAt(i) + " " + "\u001B[0m");
             out.println(); // Move to the next line after printing each row
         }

         for(int i = 0; i < 10; ++i) {
             out.print(SET_BG_COLOR_DARK_GREY + " " +  coordinates.charAt(i) + " " + "\u001B[0m");
         }

         out.println();
     }
}
