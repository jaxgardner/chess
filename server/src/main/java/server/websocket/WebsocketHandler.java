package server.websocket;

import chess.ChessGame;
import com.google.gson.Gson;
import dataAccess.AuthDAO;
import dataAccess.Exceptions.DataAccessException;
import dataAccess.GameDAO;
import dataAccess.MySql.SqlGameDao;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import webSocketMessages.serverMessages.LoadGameMessage;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.*;

import java.io.IOException;
@WebSocket
public class WebsocketHandler {
    private final ConnectionManager connections = new ConnectionManager();
    private final GameDAO sqlGameDao;
    private final AuthDAO sqlAuthDao;

    public WebsocketHandler(GameDAO sqlGameDao, AuthDAO sqlAuthDao) {
        this.sqlGameDao =  sqlGameDao;
        this.sqlAuthDao = sqlAuthDao;
    }
    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        UserGameCommand action = new Gson().fromJson(message, UserGameCommand.class);

        action = switch (action.getCommandType()) {
            case JOIN_PLAYER -> new Gson().fromJson(message, JoinPlayerCommand.class);
            case JOIN_OBSERVER -> new Gson().fromJson(message, JoinObserverCommand.class);
            case MAKE_MOVE -> new Gson().fromJson(message, MakeMoveCommand.class);
            case LEAVE -> new Gson().fromJson(message, LeaveCommand.class);
            case RESIGN -> new Gson().fromJson(message, ResignCommand.class);
        };

        switch (action.getCommandType()) {
            case JOIN_PLAYER -> joinPlayer((JoinPlayerCommand) action, session);
            case JOIN_OBSERVER -> joinObserver((JoinObserverCommand) action, session);
            case MAKE_MOVE -> makeMove(action);
            case LEAVE -> leave((LeaveCommand) action);
            case RESIGN -> resign(action);
        }
    }

    private void joinPlayer(JoinPlayerCommand command, Session session) {
        AuthData auth = verifyAuthToken(command.getAuthString());
        String username;
        if(auth != null) {
            try {
                username = auth.username();
                GameData game = sqlGameDao.getGame(command.getGameID());
                if ((game.blackUsername() != null && game.blackUsername().equals(username)) ||
                        (game.whiteUsername() != null && game.whiteUsername().equals(username))) {
                    connections.add(game.gameID(), username, session);
                }
                LoadGameMessage gameMessage = createLoadGameMessage(game);
                connections.sendToOneUser(game.gameID(), username, gameMessage);
            } catch (DataAccessException | IOException e) {
                // Switch for error message
                System.out.println("Unable to get game");
            }
        }

    }

    private void joinObserver(JoinObserverCommand command, Session session) {
        AuthData auth = verifyAuthToken(command.getAuthString());
        String username;
        if(auth != null) {
            try {
                username = auth.username();
                GameData game = sqlGameDao.getGame(command.getGameID());
                connections.add(game.gameID(), username, session);
                LoadGameMessage gameMessage = createLoadGameMessage(game);
                connections.sendToOneUser(game.gameID(), username, gameMessage);
            } catch (DataAccessException | IOException e) {
                // Switch for error message
                System.out.println("Unable to get game");
            }
        }
    }

    private void leave(LeaveCommand command) {
        AuthData auth = verifyAuthToken(command.getAuthString());
        String username;
        if(auth != null) {
            username = auth.username();
            connections.remove(command.getGameID(), username);
            try {
                GameData game = sqlGameDao.getGame(command.getGameID());
                if(game.whiteUsername() != null && game.whiteUsername().equals(username)) {
                    sqlGameDao.updateGameWhite("", game.gameID());
                } else if(game.blackUsername() != null && game.blackUsername().equals(username)) {
                    sqlGameDao.updateGameBlack("", game.gameID());
                }
            } catch (DataAccessException e) {
                System.out.println("Unable to access game");
            }
        }
    }

    private void makeMove(UserGameCommand command) {
        System.out.println(command);
    }

    private void resign(UserGameCommand command) {
        System.out.println(command);
    }

    private AuthData verifyAuthToken(String authToken) {
        try {
            return sqlAuthDao.getAuth(authToken);
        } catch (DataAccessException e) {
            System.out.println("Unable to connect");
        }
        return null;
    }

    LoadGameMessage createLoadGameMessage(GameData gameData) {
        ChessGame game = gameData.game();
        return new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, game.getBoard());
    }
}
