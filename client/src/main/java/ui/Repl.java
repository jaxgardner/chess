package ui;
import java.util.Scanner;
import static ui.EscapeSequences.*;

public class Repl {
    private final ChessClient chessClient;

    public Repl(String serverUrl) {
        chessClient = new ChessClient(serverUrl);
    }

    public void run() {
        System.out.println("Welcome to chess! Please choose from the following options: ");
        System.out.println(chessClient.printStateInfo() + "help");
        System.out.print(chessClient.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            System.out.print(chessClient.printStateInfo());
            String line = scanner.nextLine();

            try {
                result = chessClient.eval(line);
                System.out.println(result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.println(chessClient.printStateInfo() + msg);
            }
        }
        System.out.println();
    }


}
