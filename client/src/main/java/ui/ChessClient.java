package ui;

import java.util.Arrays;
import Exception.ClientException;
public class ChessClient   {
    private ClientState state = ClientState.SIGNEDOUT;

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
                case "join" -> joinGame();
                case "observe" -> observeGame();
                case "logout" -> logout();
                case "quit" -> "quit";
                default -> help();
            };
        } catch (ClientException ex) {
            return ex.getMessage();
        }
    }

    public String register (String... params) {
        return "registered";
    }

    public String login(String... params) throws ClientException {
        state = ClientState.SIGNEDIN;
        return "Logged in";
    }

    public String createGame(String... params) {
        return "Created game";
    }

    public String listGames(String... params) {
        return "List games";
    }

    public String joinGame() {
        return "Joined game";
    }

    public String observeGame(String... params) {
        return "Observing game";
    }

    public String logout() {
        state = ClientState.SIGNEDOUT;
        return "Logged out";
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

}
