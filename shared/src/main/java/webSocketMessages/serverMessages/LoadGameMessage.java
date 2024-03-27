package webSocketMessages.serverMessages;

import chess.ChessBoard;
import chess.ChessGame;

public class LoadGameMessage extends ServerMessage{
    private final ChessBoard game;

    public LoadGameMessage(ServerMessageType type, ChessBoard game) {
        super(type);
        this.game = game;
    }

    public ChessBoard getGame() {
        return game;
    }
}
