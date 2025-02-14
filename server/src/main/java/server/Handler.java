package server;

import com.google.gson.Gson;

public abstract class Handler{
    public Gson serializer = new Gson();
    public abstract Request parse(String json);
}
