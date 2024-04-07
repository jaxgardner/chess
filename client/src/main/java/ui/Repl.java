package ui;
import chess.ChessBoard;
import chess.ChessPosition;
import com.google.gson.Gson;
import ui.websocket.NotificationHandler;
import webSocketMessages.serverMessages.ErrorMessage;
import webSocketMessages.serverMessages.LoadGameMessage;
import webSocketMessages.serverMessages.NotificationMessage;
import webSocketMessages.serverMessages.ServerMessage;
import Exception.ClientException;

import java.util.Arrays;
import java.util.Scanner;

public class Repl implements NotificationHandler {
    private final ChessClient chessClient;
    private final BoardPrinter boardPrinter;
    private ChessBoard currentBoard;


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
                if(line.equals("redraw")) {
                    chessClient.printBoard(boardPrinter);
                }
                else if(line.contains("highlight")) {
                    highlight(line);
                }
                else {
                    result = chessClient.eval(line);
                    System.out.println(result);
                }
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
        currentBoard = loadGameMessage.getGame();
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

    private void highlight(String line) {
        var tokens = line.toLowerCase().split(" ");
        var params = Arrays.copyOfRange(tokens, 1, tokens.length);
        try {
            ChessPosition position = chessClient.convertPosition(params[0]);
            boardPrinter.printPossibleMoves(position, "white");
            System.out.println();
        } catch (ClientException e) {
            System.out.println("Invalid position");
        }

    }

}
