package ui.websocket;

import webSocketMessages.serverMessages.ServerMessage;

public interface NotificationHandler {
    void notify(String message);
}
