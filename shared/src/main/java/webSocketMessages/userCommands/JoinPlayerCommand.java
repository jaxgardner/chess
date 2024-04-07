package webSocketMessages.userCommands;

import chess.ChessGame;

public class JoinPlayerCommand extends UserGameCommand{
    private final int gameID;
    private final ChessGame.TeamColor playerColor;


    public JoinPlayerCommand(String authToken, CommandType commandType, int gameID, ChessGame.TeamColor playerColor) {
        super(authToken, commandType);
        this.gameID = gameID;
        this.playerColor = playerColor;
    }

    public int getGameID() {
        return gameID;
    }

    public ChessGame.TeamColor getTeamColor() {
        return playerColor;
    }
}
