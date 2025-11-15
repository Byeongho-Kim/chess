package client;

import org.junit.jupiter.api.*;
import server.Server;
import model.AuthData;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;
    private static int port;

    @BeforeAll
    public static void init() {
        server = new Server();
        port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade(port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    void clearDatabase() throws Exception {
        // Clear database before each test
        try {
            var request = java.net.http.HttpRequest.newBuilder()
                    .uri(new java.net.URI("http://localhost:" + port + "/db"))
                    .DELETE()
                    .build();
            java.net.http.HttpClient.newHttpClient().send(request,
                    java.net.http.HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
        }
    }

    @Test
    @DisplayName("Register - Positive")
    void registerPositive() throws Exception {
        var authData = facade.register("player1", "password", "p1@email.com");
        Assertions.assertNotNull(authData);
        Assertions.assertNotNull(authData.authToken());
        Assertions.assertEquals("player1", authData.username());
    }

    @Test
    @DisplayName("Register - Negative (duplicate)")
    void registerNegative() {
        Assertions.assertDoesNotThrow(() -> facade.register("player1", "password", "p1@email.com"));
        Assertions.assertThrows(Exception.class, () -> facade.register("player1", "password2", "p2@email.com"));
    }

    @Test
    @DisplayName("Login - Positive")
    void loginPositive() throws Exception {
        facade.register("player1", "password", "p1@email.com");
        var authData = facade.login("player1", "password");
        Assertions.assertNotNull(authData);
        Assertions.assertNotNull(authData.authToken());
    }

    @Test
    @DisplayName("Login - Negative (wrong password)")
    void loginNegative() throws Exception {
        facade.register("player1", "password", "p1@email.com");
        Assertions.assertThrows(Exception.class, () -> facade.login("player1", "wrongpassword"));
    }

    @Test
    @DisplayName("Logout - Positive")
    void logoutPositive() throws Exception {
        var authData = facade.register("player1", "password", "p1@email.com");
        Assertions.assertDoesNotThrow(() -> facade.logout(authData.authToken()));
    }

    @Test
    @DisplayName("Logout - Negative (invalid token)")
    void logoutNegative() {
        Assertions.assertThrows(Exception.class, () -> facade.logout("invalid-token"));
    }

    @Test
    @DisplayName("Create Game - Positive")
    void createGamePositive() throws Exception {
        var authData = facade.register("player1", "password", "p1@email.com");
        var result = facade.createGame(authData.authToken(), "TestGame");
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.gameID() > 0);
    }

    @Test
    @DisplayName("Create Game - Negative (no auth)")
    void createGameNegative() {
        Assertions.assertThrows(Exception.class, () -> facade.createGame("invalid-token", "TestGame"));
    }

    @Test
    @DisplayName("List Games - Positive")
    void listGamesPositive() throws Exception {
        var authData = facade.register("player1", "password", "p1@email.com");
        facade.createGame(authData.authToken(), "Game1");
        facade.createGame(authData.authToken(), "Game2");

        var result = facade.listGames(authData.authToken());
        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result.games().length);
    }

    @Test
    @DisplayName("List Games - Negative (no auth)")
    void listGamesNegative() {
        Assertions.assertThrows(Exception.class, () -> facade.listGames("invalid-token"));
    }

    @Test
    @DisplayName("Join Game - Positive")
    void joinGamePositive() throws Exception {
        var authData = facade.register("player1", "password", "p1@email.com");
        var game = facade.createGame(authData.authToken(), "TestGame");
        Assertions.assertDoesNotThrow(() -> facade.joinGame(authData.authToken(), game.gameID(), "WHITE"));
    }

    @Test
    @DisplayName("Join Game - Negative (no auth)")
    void joinGameNegative() throws Exception {
        var authData = facade.register("player1", "password", "p1@email.com");
        var game = facade.createGame(authData.authToken(), "TestGame");
        Assertions.assertThrows(Exception.class, () -> facade.joinGame("invalid-token", game.gameID(), "WHITE"));
    }
}
