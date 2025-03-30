package websocket.messages;

public class ErrorMessage extends ServerMessage{
    private String errorMessage;
    public ErrorMessage(String msg) {
        super(ServerMessageType.ERROR);
        this.errorMessage = msg;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
