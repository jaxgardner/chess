import chess.*;
import ui.BoardPrinter;
import ui.Repl;

public class Main {
    public static void main(String[] args) {
        var repl = new Repl("http://localhost:8080/");
        repl.run();
    }
}