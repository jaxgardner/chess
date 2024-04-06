package ui.websocket;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import webSocketMessages.serverMessages.ServerMessage;
import Exception.ClientException;
import webSocketMessages.userCommands.*;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketFacade extends Endpoint {
    Session session;
    NotificationHandler notificationHandler;

    public WebSocketFacade(String url, NotificationHandler notificationHandler) throws ClientException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "connect");
            this.notificationHandler = notificationHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    notificationHandler.notify(message);
                }
            });

        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ClientException(ex.getMessage());
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void joinGameAsPlayer(String authToken, int gameID, ChessGame.TeamColor color) throws ClientException {
        try {
            var command = new JoinPlayerCommand(authToken, UserGameCommand.CommandType.JOIN_PLAYER, gameID, color);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException ex) {
            throw new ClientException(ex.getMessage());
        }
    }

    public void joinGameAsObserver(String authToken, int gameID) throws ClientException {
        try {
            var command = new JoinObserverCommand(authToken, UserGameCommand.CommandType.JOIN_OBSERVER, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException ex) {
            throw new ClientException(ex.getMessage());
        }
    }

    public void leaveGame(String authToken, int gameID) throws ClientException {
        try {
            var command = new LeaveCommand(authToken, UserGameCommand.CommandType.LEAVE, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException ex) {
            throw new ClientException(ex.getMessage());
        }
    }

    public void makeMove(String authToken, int gameID, ChessMove chessMove) throws ClientException {
        try {
            var command = new MakeMoveCommand(authToken, UserGameCommand.CommandType.MAKE_MOVE, gameID, chessMove);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException ex) {
            throw new ClientException(ex.getMessage());
        }
    }

    public void resign(String authToken, int gameID) throws ClientException {
        try {
            var command = new ResignCommand(authToken, UserGameCommand.CommandType.RESIGN, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException ex) {
            throw new ClientException(ex.getMessage());
        }
    }

}
