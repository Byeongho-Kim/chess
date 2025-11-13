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
    public void sampleTest() {
        Assertions.assertTrue(true);
    }

}
