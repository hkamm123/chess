package server;

public class RegisterHandler extends Handler {
    @Override
    public Request parse(String json) {
        return (RegisterRequest) serializer.fromJson(json, RegisterRequest.class);
    }
}
