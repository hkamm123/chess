package websocket.messages;

import chess.ChessGame;

public class LoadGameMessage extends ServerMessage{
    private final String game; //json string of the game

    public LoadGameMessage(String gameJson) {
        super(ServerMessageType.LOAD_GAME);
        this.game = gameJson;
    }

    public String getGame() {
        return this.game;
    }
}
