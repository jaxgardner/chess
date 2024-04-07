package ui;
import chess.ChessBoard;
import chess.ChessGame;
import com.google.gson.Gson;
import ui.websocket.NotificationHandler;
import webSocketMessages.serverMessages.ErrorMessage;
import webSocketMessages.serverMessages.LoadGameMessage;
import webSocketMessages.serverMessages.NotificationMessage;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.*;

import java.util.Scanner;
import static ui.EscapeSequences.*;

public class Repl implements NotificationHandler {
    private final ChessClient chessClient;
    private final BoardPrinter boardPrinter;

    public Repl(String serverUrl) {
        chessClient = new ChessClient(serverUrl, this);
        boardPrinter = new BoardPrinter();
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

    public void notify(String message) {
       ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
        switch (serverMessage.getServerMessageType()) {
            case LOAD_GAME:
                loadGamePrint(message);
                break;
            case ERROR:
                printErrorMessage(message);
                break;
            case NOTIFICATION:
                printNotification(message);
                break;
        }
    }

    private void loadGamePrint(String message) {
        LoadGameMessage loadGameMessage = new Gson().fromJson(message, LoadGameMessage.class);
        System.out.println();
        System.out.println(chessClient.help());
        System.out.println("Current board:");
        System.out.println();
        boardPrinter.setBoard(loadGameMessage.getGame());
        boardPrinter.printChessBoard(chessClient.getPlayerGameColor());
        System.out.println();
        System.out.print(chessClient.printStateInfo());
    }

    private void printNotification(String message) {
        NotificationMessage notificationMessage = new Gson().fromJson(message, NotificationMessage.class);
        System.out.println();
        System.out.println(notificationMessage.getNotificationMessage());
        System.out.print(chessClient.printStateInfo());
    }

    private void printErrorMessage(String message) {
        ErrorMessage errorMessage = new Gson().fromJson(message, ErrorMessage.class);
        System.out.println();
        System.out.println(errorMessage.getErrorMessage());
        System.out.print(chessClient.printStateInfo());
    }

}
