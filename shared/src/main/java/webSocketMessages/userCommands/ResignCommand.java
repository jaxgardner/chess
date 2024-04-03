package webSocketMessages.userCommands;

public class ResignCommand extends UserGameCommand{
    private final int gameID;
    public ResignCommand(String authToken, CommandType commandType, int gameID) {
        super(authToken, commandType);
        this.gameID = gameID;
    }

    public int getGameID() {
        return gameID;
    }
}