package webSocketMessages.userCommands;

public class LeaveCommand extends UserGameCommand{
    private final int gameID;
    public LeaveCommand(String authToken, CommandType commandType, int gameID) {
        super(authToken, commandType);
        this.gameID = gameID;
    }

    public int getGameID() {
        return gameID;
    }
}
