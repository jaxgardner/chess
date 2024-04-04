package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import webSocketMessages.userCommands.*;

import java.io.IOException;
@WebSocket
public class WebsocketHandler {
    private final ConnectionManager connections = new ConnectionManager();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        UserGameCommand action = new Gson().fromJson(message, UserGameCommand.class);

        switch (action.getCommandType()) {
            case JOIN_PLAYER:
                action = new Gson().fromJson(message, JoinPlayerCommand.class);
            case JOIN_OBSERVER:
                action = new Gson().fromJson(message, JoinObserverCommand.class);
            case MAKE_MOVE:
                action = new Gson().fromJson(message, MakeMoveCommand.class);
            case LEAVE:
                action = new Gson().fromJson(message, LeaveCommand.class);
            case RESIGN:
                action = new Gson().fromJson(message, ResignCommand.class);
        }

        switch (action.getCommandType()) {
            case JOIN_PLAYER -> joinPlayer(action);
            case JOIN_OBSERVER -> joinObserver(action);
            case MAKE_MOVE -> makeMove(action);
            case LEAVE -> leave(action);
            case RESIGN -> resign(action);
        }
    }

    private void joinPlayer(UserGameCommand command) {
        System.out.println(command);
    }

    private void joinObserver(UserGameCommand command) {
        System.out.println(command);
    }
    private void makeMove(UserGameCommand command) {
        System.out.println(command);
    }

    private void leave(UserGameCommand command) {
        System.out.println(command);
    }

    private void resign(UserGameCommand command) {
        System.out.println(command);
    }
}
