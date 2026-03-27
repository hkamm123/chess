package server;

import io.javalin.websocket.WsMessageContext;

public class WebsocketHandler {
    public void handleMessage(WsMessageContext ctx) {
        ctx.send("Websocket response: " + ctx.message());
    }
}
