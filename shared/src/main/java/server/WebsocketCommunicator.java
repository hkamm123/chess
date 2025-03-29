package server;

import com.google.gson.Gson;
import websocket.messages.ErrorMessage;
import websocket.messages.ServerMessage;

public class WebsocketCommunicator {
    private String serverURL;
    private Gson gson;
    private ServerMessageObserver observer;

    public WebsocketCommunicator(String serverURL, ServerMessageObserver observer) {
        this.serverURL = serverURL;
        this.observer = observer;
        this.gson = new Gson();
    }

    public void onMessage(String message) {
        try {
            ServerMessage msg = gson.fromJson(message, ServerMessage.class);
            observer.notify(msg);
        } catch (Exception ex) {
            observer.notify(new ErrorMessage(ex.getMessage()));
        }
    }
}
