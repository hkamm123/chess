package websocket.messages;

public class ErrorMessage extends ServerMessage{
    private String errorMessage;
    public ErrorMessage(ServerMessageType type, String msg) {
        super(ServerMessageType.ERROR);
        this.errorMessage = msg;
    }
}
