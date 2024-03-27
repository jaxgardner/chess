package webSocketMessages.userCommands;

public class JoinObserverCommand extends UserGameCommand{
    private final int gameID;

    public JoinObserverCommand(String authToken, CommandType commandType, int gameID) {
        super(authToken, commandType);
        this.gameID = gameID;
    }

    public int getGameID() {
        return gameID;
    }
}
