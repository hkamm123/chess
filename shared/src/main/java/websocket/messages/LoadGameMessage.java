package websocket.messages;

import chess.ChessGame;

public class LoadGameMessage extends ServerMessage{
    private String game; //json string of the game

    public LoadGameMessage(String gameJson) {
        super(ServerMessageType.LOAD_GAME);
        game = gameJson;
    }

    public String getGame() {
        return game;
    }
}
