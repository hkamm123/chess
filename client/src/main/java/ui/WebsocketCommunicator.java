package ui;

import com.google.gson.Gson;
import websocket.messages.ErrorMessage;

import javax.websocket.*;

import java.net.URI;

public class WebsocketCommunicator extends Endpoint {
    private String serverURL;
    private Gson serializer;
    private ServerMessageObserver observer;
    private Session session;

    public WebsocketCommunicator(String serverURL, ServerMessageObserver observer) {
        serverURL = serverURL.replace("http", "ws");
        try {
            URI socketURI = new URI(serverURL + "/ws");
            this.observer = observer;
            this.serializer = new Gson();

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error creating Websocket communicator.");
        }
    }

    public void send(String msg) throws Exception {
        this.session.getBasicRemote().sendText(msg);
    }

    public void onOpen(Session session, EndpointConfig endpointConfig) {
        this.session = session;
        this.session.addMessageHandler(new MessageHandler.Whole<String>() {
            public void onMessage(String message) {
                try {
                    observer.notify(message);
                } catch (Exception ex) {
                    ErrorMessage err = new ErrorMessage(ex.getMessage());
                    observer.notify(serializer.toJson(err));
                }
            }
        });
    }
}
