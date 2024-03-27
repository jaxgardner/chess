package webSocketMessages.serverMessages;

public class NotificationMessage extends ServerMessage {
    private final String notificationMessage;

    public NotificationMessage(ServerMessageType type, String message) {
        super(type);
        notificationMessage = message;
    }

    public String getNotificationMessage() {
        return notificationMessage;
    }
}
