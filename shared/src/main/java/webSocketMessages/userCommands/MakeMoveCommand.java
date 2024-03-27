package webSocketMessages.userCommands;

import chess.ChessMove;

public class MakeMoveCommand extends UserGameCommand{
    private final int gameID;
    private final ChessMove chessMove;

    public MakeMoveCommand(String authToken, CommandType commandType, int gameID, ChessMove chessMove) {
        super(authToken, commandType);
        this.gameID = gameID;
        this.chessMove = chessMove;
    }

    public int getGameID() {
        return gameID;
    }

    public ChessMove getChessMove() {
        return chessMove;
    }
}
