package service;

import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTest {
    @Test
    public void testListGamesSuccess() throws Exception {
        DataAccess dataAccess = new MemoryDataAccess();
        GameService gameService = new GameService(dataAccess);
        
        UserData user = new UserData("testuser", "password", "test@example.com");
        dataAccess.createUser(user);
        String authToken = "test123";
        AuthData auth = new AuthData(authToken, "testuser");
        dataAccess.createAuth(auth);

        GameService.ListGamesResult result = gameService.listGames(authToken);

        assertTrue(result.games.size() == 0);
    }

    @Test
    public void testListGamesUnauthorized() throws Exception {
        DataAccess dataAccess = new MemoryDataAccess();
        GameService gameService = new GameService(dataAccess);

        try {
            gameService.listGames("invalid-token");
            fail("Should have thrown exception");
        }
        catch (ServiceException e) {
            assertTrue(e.getMessage().contains("unauthorized"));
        }
    }

    @Test
    public void testCreateGameSuccess() throws Exception {
        DataAccess dataAccess = new MemoryDataAccess();
        GameService gameService = new GameService(dataAccess);

        UserData user = new UserData("testuser", "password", "test@example.com");
        dataAccess.createUser(user);
        String authToken = "test123";
        AuthData auth = new AuthData(authToken, "testuser");
        dataAccess.createAuth(auth);

        GameService.CreateGameResult result = gameService.createGame(authToken, "Test Game");

        assertTrue(result.gameID > 0);
    }

    @Test
    public void testCreateGameUnauthorized() throws Exception {
        DataAccess dataAccess = new MemoryDataAccess();
        GameService gameService = new GameService(dataAccess);

        try {
            gameService.createGame("invalid-token", "Test Game");
            fail("Should have thrown exception");
        }
        catch (ServiceException e) {
            assertTrue(e.getMessage().contains("unauthorized"));
        }
    }

    @Test
    public void testCreateGameBadRequest() throws Exception {
        DataAccess dataAccess = new MemoryDataAccess();
        GameService gameService = new GameService(dataAccess);

        UserData user = new UserData("testuser", "password", "test@example.com");
        dataAccess.createUser(user);
        String authToken = "test-token-123";
        AuthData auth = new AuthData(authToken, "testuser");
        dataAccess.createAuth(auth);

        try {
            gameService.createGame(authToken, null);
            fail("Should have thrown exception");
        }
        catch (ServiceException e) {
            assertTrue(e.getMessage().contains("bad request"));
        }
    }

    @Test
    public void testJoinGameSuccess() throws Exception {
        DataAccess dataAccess = new MemoryDataAccess();
        GameService gameService = new GameService(dataAccess);

        UserData user = new UserData("testuser", "password", "test@example.com");
        dataAccess.createUser(user);
        String authToken = "test123";
        AuthData auth = new AuthData(authToken, "testuser");
        dataAccess.createAuth(auth);

        GameService.CreateGameResult createResult = gameService.createGame(authToken, "Test Game");

        gameService.joinGame(authToken, "WHITE", createResult.gameID);
        assertTrue(true);
    }

    @Test
    public void testJoinGameColorAlreadyTaken() throws Exception {
        DataAccess dataAccess = new MemoryDataAccess();
        GameService gameService = new GameService(dataAccess);

        UserData user = new UserData("testuser", "password", "test@example.com");
        dataAccess.createUser(user);
        String authToken = "test123";
        AuthData auth = new AuthData(authToken, "testuser");
        dataAccess.createAuth(auth);

        GameService.CreateGameResult createResult = gameService.createGame(authToken, "Test Game");

        gameService.joinGame(authToken, "WHITE", createResult.gameID);

        try {
            gameService.joinGame(authToken, "WHITE", createResult.gameID);
            fail("Should have thrown exception");
        }
        catch (ServiceException e) {
            assertTrue(e.getMessage().contains("already taken"));
        }
    }

    @Test
    public void testJoinGameBadRequest() throws Exception {
        DataAccess dataAccess = new MemoryDataAccess();
        GameService gameService = new GameService(dataAccess);

        UserData user = new UserData("testuser", "password", "test@example.com");
        dataAccess.createUser(user);
        String authToken = "test-token-123";
        AuthData auth = new AuthData(authToken, "testuser");
        dataAccess.createAuth(auth);

        try {
            gameService.joinGame(authToken, "INVALID", 1);
            fail("Should have thrown exception");
        }
        catch (ServiceException e) {
            assertTrue(e.getMessage().contains("bad request"));
        }
    }
}