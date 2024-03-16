package ui;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import Exception.ClientException;
import model.*;

public class ChessClient   {
    private ClientState state = ClientState.SIGNEDOUT;
    private final BoardPrinter chessPrinter;
    private final ServerFacade server;
    public ChessClient(String serverUrl) {
        chessPrinter = new BoardPrinter();
        server = new ServerFacade(serverUrl);
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "register" -> register(params);
                case "login" -> login(params);
                case "create" -> createGame(params);
                case "list" -> listGames();
                case "join" -> joinGame(params);
                case "observe" -> observeGame(params);
                case "logout" -> logout();
                case "quit" -> "quit";
                default -> help();
            };
        } catch (ClientException ex) {
            return ex.getMessage();
        }
    }

    public String register (String... params) throws ClientException{
        String username;
        String password;
        String email;
        try {
            username = params[0];
            password = params[1];
            email = params[2];
        } catch(Exception e) {
            throw new ClientException("Invalid arguments");
        }

        UserData user = new UserData(username, password, email);

        String result = server.registerUser(user);
        if(Objects.equals(result, "Registered!")) {
            state = ClientState.SIGNEDIN;
        }
        System.out.println(result);
        return help();
    }

    public String login(String... params) throws ClientException {
        String username;
        String password;

        try {
            username = params[0];
            password = params[1];
        } catch (Exception e) {
            throw new ClientException("Invalid arguments");
        }

        LoginRequest loginRequest = new LoginRequest(username, password);
        state = ClientState.SIGNEDIN;

        String result = server.loginUser(loginRequest);
        if(result.equals("Logged in!")) {
            state = ClientState.SIGNEDIN;
        }
        return result;
    }

    public String createGame(String... params) throws ClientException {
        assertSignedIn();
        String gameName;
        try {
            gameName = params[0];
        } catch (Exception e) {
            throw new ClientException("Invalid arguments");
        }

        String result = server.createGame(new CreateGameRequest(gameName));

        return result;
    }

    public String listGames() throws ClientException {
        assertSignedIn();
        List<GameListResult> res = server.listGames();
        if(res.isEmpty()) {
            return "No games to display";
        }
        StringBuilder games = new StringBuilder();

        for(int i = 1; i <= res.size(); i++) {
            String num = i + ". ";
            String gameName = res.get(i - 1).gameName();
            String whiteP = res.get(i - 1).whiteUsername();
            whiteP = (whiteP != null) ? whiteP : "None";
            String blackP = res.get(i - 1).blackUsername();
            blackP = (blackP != null) ? blackP : "None";
            games.append(num).append(gameName).append(" White Player: ").append(whiteP).append(", ").append(" Black Player: ").append(blackP).append('\n');
        }


        return games.toString();
    }

    public String joinGame(String... params) throws ClientException {
        assertSignedIn();
        String playerColor;
        int gameID;

        try {
            gameID = Integer.parseInt(params[0]);
        } catch (Exception e) {
            throw new ClientException("Invalid arguments");
        }

        if(params.length >= 2 ) {
            playerColor = params[1];

            if(!playerColor.equalsIgnoreCase("white") && !playerColor.equalsIgnoreCase("black")) {
                throw new ClientException("Invalid arguments");
            }
        } else {
            playerColor=null;
        }

        JoinGameRequest gameRequest = new JoinGameRequest(playerColor, gameID);
        String res = server.joinGame(gameRequest);
        chessPrinter.printChessBoard("black");
        System.out.println();
        chessPrinter.printChessBoard("white");
        System.out.println();
        return res;
    }

    public String observeGame(String... params) throws ClientException {
        assertSignedIn();
        int gameID;

        try {
            gameID = Integer.parseInt(params[0]);
        } catch(Exception e) {
            throw new ClientException("Invalid arguments");
        }

        server.joinGame(new JoinGameRequest("", gameID) );

        chessPrinter.printChessBoard("white");
        System.out.println();
        chessPrinter.printChessBoard("black");
        System.out.println();
        return "Observing game";
    }

    public String logout() throws ClientException {
        assertSignedIn();
        String result = server.logoutUser();

        if(result.equals("Logged out!")) {
            state = ClientState.SIGNEDOUT;
        }
        System.out.println(result);
        System.out.println("Current options: ");
        return help();
    }

    public String help() {
        if(state == ClientState.SIGNEDOUT) {
            return """
                    register <USERNAME> <PASSWORD> <EMAIL> - to create an account
                    login <USERNAME> <PASSWORD> - to play chess
                    quit - stop the program
                    help - with possible commands
                    """.indent(4);
        }
        return """
                create <NAME> - a game
                list - games
                join <ID> [WHITE|BLACK|<empty>] - a game
                observe <ID> - a game
                logout - when you are done
                quit - stop the program
                help - with possible commands
                """.indent(4);
    }

    public String printStateInfo() {
        if(state == ClientState.SIGNEDOUT) {
            return "[LOGGED_OUT] >>> ";
        }
        return "[LOGGED_IN] >>> ";
    }

    private void assertSignedIn() throws ClientException {
        if (state == ClientState.SIGNEDOUT) {
            throw new ClientException("You must sign in");
        }
    }

}
