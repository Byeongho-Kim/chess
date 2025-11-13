package client;

import com.google.gson.Gson;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import model.AuthData;

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

    // Request/Response records
    private record RegisterRequest(String username, String password, String email) {}

    // Register a new user
    public AuthData register(String username, String password, String email) throws Exception {
        var request = buildRequest("POST", "/user", new RegisterRequest(username, password, email), null);
        var response = sendRequest(request);
        return handleResponse(response, AuthData.class);
    }

    private record LoginRequest(String username, String password) {}

    // Login existing user
    public AuthData login(String username, String password) throws Exception {
        var request = buildRequest("POST", "/session", new LoginRequest(username, password), null);
        var response = sendRequest(request);
        return handleResponse(response, AuthData.class);
    }

    // Logout user
    public void logout(String authToken) throws Exception {
        var request = buildRequest("DELETE", "/session", null, authToken);
        var response = sendRequest(request);
        handleResponse(response, null);
    }
}