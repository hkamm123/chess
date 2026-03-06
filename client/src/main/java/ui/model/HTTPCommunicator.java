package ui.model;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;

public class HTTPCommunicator {
    private String serverUrl;

    private static final HttpClient httpClient = HttpClient.newHttpClient();

    public HTTPCommunicator(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public <T> T sendRequest(Class<T> responseClass, String method, String path, Object requestBody) throws HttpResponseException {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(serverUrl + path))
                    .method(method, HttpRequest.BodyPublishers.ofString(new Gson().toJson(requestBody)))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return new Gson().fromJson(response.body(), responseClass);
            } else {
                String body = response.body();
                var map = new Gson().fromJson(body, HashMap.class);
                String message = map.get("message").toString();
                throw new HttpResponseException(response.statusCode(), message);
            }
        } catch (IOException | InterruptedException | URISyntaxException ex) {
            throw new HttpResponseException(500, ex.getMessage());
        }
    }

    public <T> T sendRequest(Class<T> responseClass, String method, String path, Object requestBody, String authToken) throws HttpResponseException {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(serverUrl + path))
                    .method(method, HttpRequest.BodyPublishers.ofString(new Gson().toJson(requestBody)))
                    .header("Authorization", authToken)
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return new Gson().fromJson(response.body(), responseClass);
            } else {
                String body = response.body();
                var map = new Gson().fromJson(body, HashMap.class);
                String message = map.get("message").toString();
                throw new HttpResponseException(response.statusCode(), message);
            }
        } catch (IOException | InterruptedException | URISyntaxException ex) {
            throw new HttpResponseException(500, ex.getMessage());
        }
    }

    public void sendRequest(String method, String path, String authToken) throws HttpResponseException {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(serverUrl + path))
                    .method(method, HttpRequest.BodyPublishers.noBody())
                    .header("Authorization", authToken)
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                String body = response.body();
                var map = new Gson().fromJson(body, HashMap.class);
                String message = map.get("message").toString();
                throw new HttpResponseException(response.statusCode(), message);
            }
        } catch (IOException | InterruptedException | URISyntaxException ex) {
            throw new HttpResponseException(500, ex.getMessage());
        }
    }
}
