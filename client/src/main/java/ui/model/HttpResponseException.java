package ui.model;

public class HttpResponseException extends Exception {
    private final int statusCode;

    public HttpResponseException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
