package ui;

import com.google.gson.Gson;
import org.glassfish.tyrus.core.wsadl.model.Endpoint;
import server.ServerMessageObserver;
import websocket.messages.ErrorMessage;
import websocket.messages.ServerMessage;
import javax.websocket.*;

import java.net.URI;

public class WebsocketCommunicator extends Endpoint {
    private String serverURL;
    private Gson gson;
    private ServerMessageObserver observer;
    private Session session;

    public WebsocketCommunicator(String serverURL, ServerMessageObserver observer) {
        serverURL = serverURL.replace("http", "ws");
        try {
            URI socketURI = new URI(serverURL + "/ws");
            this.observer = observer;
            this.gson = new Gson();

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                public void onMessage(String message) {
                    try {
                        ServerMessage msg = gson.fromJson(message, ServerMessage.class);
                        observer.notify(msg);
                    } catch (Exception ex) {
                        observer.notify(new ErrorMessage(ex.getMessage()));
                    }
                }
            });
        } catch (Exception ex) {
            System.out.println("Error creating Websocket communicator.");
        }
    }

    public void send(String msg) throws Exception {
        this.session.getBasicRemote().sendText(msg);
    }

    public void onOpen(Session session, EndpointConfig endpointConfig) {}
}
