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
import model.GameData;

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
            String errorMessage = extractErrorMessage(response.body(), status);
            throw new Exception(errorMessage);
        }

        if (responseClass != null) {
            return new Gson().fromJson(response.body(), responseClass);
        }
        return null;
    }

    // Helper method to extract user-friendly error messages
    private String extractErrorMessage(String responseBody, int statusCode) {
        try {
            var errorResponse = new Gson().fromJson(responseBody, ErrorResponse.class);
            if (errorResponse != null && errorResponse.message != null) {
                String message = errorResponse.message;
                if (message.startsWith("Error: ")) {
                    message = message.substring(7);
                }
                return message;
            }
        } catch (Exception e) {
        }

        return switch (statusCode) {
            case 400 -> "Bad request. Please check your input and try again.";
            case 401 -> "Invalid username or password.";
            case 403 -> "Access denied. That username or spot may already be taken.";
            case 404 -> "Not found. Please check your request.";
            case 500 -> "Server error. Please try again later.";
            default -> "Request failed. Please try again.";
        };
    }

    private record ErrorResponse(String message) {}

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

    private record CreateGameRequest(String gameName) {}
    public record CreateGameResult(int gameID) {}

    // Create a new game
    public CreateGameResult createGame(String authToken, String gameName) throws Exception {
        var request = buildRequest("POST", "/game", new CreateGameRequest(gameName), authToken);
        var response = sendRequest(request);
        return handleResponse(response, CreateGameResult.class);
    }

    public record ListGamesResult(GameData[] games) {}

    // List all games
    public ListGamesResult listGames(String authToken) throws Exception {
        var request = buildRequest("GET", "/game", null, authToken);
        var response = sendRequest(request);
        return handleResponse(response, ListGamesResult.class);
    }

    private record JoinGameRequest(String playerColor, int gameID) {}

    // Join a game
    public void joinGame(String authToken, int gameID, String playerColor) throws Exception {
        var request = buildRequest("PUT", "/game", new JoinGameRequest(playerColor, gameID), authToken);
        var response = sendRequest(request);
        handleResponse(response, null);
    }
}