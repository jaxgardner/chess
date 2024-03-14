import chess.*;
import ui.BoardPrinter;

public class Main {
    public static void main(String[] args) {
        var chessPrinter = new BoardPrinter();
        chessPrinter.printChessBoard("white");
        System.out.println();
        chessPrinter.printChessBoard("black");
    }
}