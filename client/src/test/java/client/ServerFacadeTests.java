package client;

import org.junit.jupiter.api.*;
import server.Server;
import model.AuthData;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
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
                    .uri(new java.net.URI("http://localhost:" + server.port() + "/db"))
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
    public void sampleTest() {
        Assertions.assertTrue(true);
    }

}
