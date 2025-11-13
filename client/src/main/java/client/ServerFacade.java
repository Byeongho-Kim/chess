package client;

import com.google.gson.Gson;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

public class ServerFacade {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public ServerFacade(int port) {
        serverUrl = "http://localhost:" + port;
    }
    // Helper to build HTTP requests
    private HttpRequest buildRequest(String method, String path, Object body, String authToken) {
        var builder = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + path))
                .method(method, makeRequestBody(body));

        if (body != null) {
            builder.setHeader("Content-Type", "application/json");
        }
        if (authToken != null) {
            builder.setHeader("Authorization", authToken);
        }

        return builder.build();
    }

    // Helper to create request body
    private BodyPublisher makeRequestBody(Object body) {
        if (body != null) {
            return BodyPublishers.ofString(new Gson().toJson(body));
        }
        return BodyPublishers.noBody();
    }

    // Helper to send request
    private HttpResponse<String> sendRequest(HttpRequest request) throws Exception {
        try {
            return client.send(request, BodyHandlers.ofString());
        }
        catch (Exception ex) {
            throw new Exception("Server error: " + ex.getMessage());
        }
    }

    // Helper to handle response
    private <T> T handleResponse(HttpResponse<String> response, Class<T> responseClass) throws Exception {
        int status = response.statusCode();
        if (!isSuccessful(status)) {
            throw new Exception("Error: " + response.body());
        }

        if (responseClass != null) {
            return new Gson().fromJson(response.body(), responseClass);
        }
        return null;
    }

    // Check if status code is successful
    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}