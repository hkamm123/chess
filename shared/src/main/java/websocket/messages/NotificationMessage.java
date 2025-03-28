package websocket.messages;

public class NotificationMessage extends ServerMessage {
    private String message;
    public NotificationMessage(ServerMessageType type, String msg) {
        super(ServerMessageType.NOTIFICATION);
        this.message = msg;
    }
}
