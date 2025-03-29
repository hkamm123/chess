package websocket.messages;

public class LoadGameMessage extends ServerMessage{
    private String game; //json string of the game

    public LoadGameMessage(String gameJson) {
        super(ServerMessageType.LOAD_GAME);
        game = gameJson;
    }
}
