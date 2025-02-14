package server;

import com.google.gson.Gson;

public abstract class Handler {
    private Gson serializer = new Gson();
    public Handler() {

    }
    public abstract Request parse(String json);
}
