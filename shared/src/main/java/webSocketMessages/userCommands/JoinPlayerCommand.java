package webSocketMessages.userCommands;

import chess.ChessGame;

public class JoinPlayerCommand extends UserGameCommand{
    private final int gameID;
    private final ChessGame.TeamColor teamColor;


    public JoinPlayerCommand(String authToken, CommandType commandType, int gameID, ChessGame.TeamColor teamColor) {
        super(authToken, commandType);
        this.gameID = gameID;
        this.teamColor = teamColor;
    }

    public int getGameID() {
        return gameID;
    }

    public ChessGame.TeamColor getTeamColor() {
        return teamColor;
    }
}
