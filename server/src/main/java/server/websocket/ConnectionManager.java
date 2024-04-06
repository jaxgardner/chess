package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import webSocketMessages.serverMessages.ServerMessage;

import javax.management.Notification;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<Integer, ArrayList<Connection>> gameConnections = new ConcurrentHashMap<>();

    public void add(Integer gameID, String username, Session session) {
        var connection = new Connection(username, session);
        if(gameConnections.containsKey(gameID)) {
            var gameObservers = gameConnections.get(gameID);
            gameObservers.add(connection);
        } else {
            ArrayList<Connection> gamePlayer = new ArrayList<>();
            gamePlayer.add(connection);
            gameConnections.put(gameID, gamePlayer);
        }
    }

    public void remove(Integer gameID, String username) {
        var gameParticipants = gameConnections.get(gameID);
        gameParticipants.removeIf(participant -> participant.username.equals(username));
    }

    public void broadcast(Integer gameID, String excludeUsername, Notification notification) throws IOException {
        var removeList = new ArrayList<Connection>();
        var gameParticipants = gameConnections.get(gameID);
        for (var c : gameParticipants) {
            if (c.session.isOpen()) {
                if (!c.username.equals(excludeUsername)) {
                    c.send(notification.toString());
                }
            } else {
                removeList.add(c);
            }
        }

        // Clean up any connections that were left open.
        for (var c : removeList) {
            remove(gameID, c.username);
        }
    }

    public void sendToOneUser(Integer gameID, String username, ServerMessage serverMessage) throws IOException {
        var gameParticipants = gameConnections.get(gameID);
        for (var c : gameParticipants) {
            if (c.session.isOpen()) {
                if (c.username.equals(username)) {
                    String message = new Gson().toJson(serverMessage);
                    c.send(message);
                }
            } else {
                remove(gameID, username);
            }
        }
    }
}
