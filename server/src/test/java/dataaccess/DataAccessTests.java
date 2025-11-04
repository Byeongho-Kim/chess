package dataaccess;

import chess.ChessGame;
import model.GameData;
import model.AuthData;
import model.UserData;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

import static org.junit.jupiter.api.Assertions.*;

public class DataAccessTests {
    private DataAccess dataAccess;

    @BeforeEach
    void setUp() throws DataAccessException {
        dataAccess = new MySQLDataAccess();
        dataAccess.clearUsers();
        dataAccess.clearGames();
        dataAccess.clearAuths();
    }

    @AfterEach
    void tearDown() throws DataAccessException {
        dataAccess.clearUsers();
        dataAccess.clearGames();
        dataAccess.clearAuths();
    }

    // USER

    @Test
    void createUserPositive() throws DataAccessException {
        UserData user = new UserData("testuser", "password123", "test@email.com");
        dataAccess.createUser(user);

        UserData result = dataAccess.getUser("testuser");
        assertNotNull(result);
        assertEquals("testuser", result.username());
        assertEquals("test@email.com", result.email());
        assertTrue(BCrypt.checkpw("password123", result.password()));
    }

    @Test
    void createUserNegative() throws DataAccessException {
        UserData user = new UserData("testuser", "password123", "test@email.com");
        dataAccess.createUser(user);

        assertThrows(DataAccessException.class, () -> dataAccess.createUser(user));
    }

    @Test
    void getUserPositive() throws DataAccessException {
        UserData user = new UserData("testuser", "password123", "test@email.com");
        dataAccess.createUser(user);

        UserData result = dataAccess.getUser("testuser");
        assertNotNull(result);
        assertEquals("testuser", result.username());
    }

    @Test
    void getUserNegative() throws DataAccessException {
        UserData result = dataAccess.getUser("nonexistent");
        assertNull(result);
    }

    @Test
    void clearUsers() throws DataAccessException {
        UserData user = new UserData("testuser", "password123", "test@email.com");
        dataAccess.createUser(user);

        dataAccess.clearUsers();

        assertNull(dataAccess.getUser("testuser"));
    }

    // AUTH

    @Test
    void createAuthPositive() throws DataAccessException {
        AuthData auth = new AuthData("token123", "testuser");
        dataAccess.createAuth(auth);

        AuthData result = dataAccess.getAuth("token123");
        assertNotNull(result);
        assertEquals("token123", result.authToken());
        assertEquals("testuser", result.username());
    }

    @Test
    void createAuthNegative() throws DataAccessException {
        AuthData auth = new AuthData("token123", "testuser");
        dataAccess.createAuth(auth);

        assertThrows(DataAccessException.class, () -> dataAccess.createAuth(auth));
    }

    @Test
    void getAuthPositive() throws DataAccessException {
        AuthData auth = new AuthData("token123", "testuser");
        dataAccess.createAuth(auth);

        AuthData result = dataAccess.getAuth("token123");
        assertNotNull(result);
        assertEquals("testuser", result.username());
    }

    @Test
    void getAuthNegative() throws DataAccessException {
        AuthData result = dataAccess.getAuth("nonexistent");
        assertNull(result);
    }

    @Test
    void deleteAuthPositive() throws DataAccessException {
        AuthData auth = new AuthData("token123", "testuser");
        dataAccess.createAuth(auth);

        dataAccess.deleteAuth("token123");
        assertNull(dataAccess.getAuth("token123"));
    }

    @Test
    void deleteAuthNegative() throws DataAccessException {
        assertDoesNotThrow(() -> dataAccess.deleteAuth("nonexistent"));
    }

    @Test
    void clearAuths() throws DataAccessException {
        AuthData auth = new AuthData("token123", "testuser");
        dataAccess.createAuth(auth);

        dataAccess.clearAuths();

        assertNull(dataAccess.getAuth("token123"));
    }

    // GAME

    @Test
    void createGamePositive() throws DataAccessException {
        ChessGame game = new ChessGame();
        GameData gameData = new GameData(1, null, null, "TestGame", game);
        dataAccess.createGame(gameData);

        GameData result = dataAccess.getGame(1);
        assertNotNull(result);
        assertEquals("TestGame", result.gameName());
        assertNotNull(result.game());
    }

    @Test
    void createGameNegative() throws DataAccessException {
        ChessGame game = new ChessGame();
        GameData gameData = new GameData(1, null, null, "TestGame", game);
        dataAccess.createGame(gameData);

        assertThrows(DataAccessException.class, () -> dataAccess.createGame(gameData));
    }

    @Test
    void getGamePositive() throws DataAccessException {
        ChessGame game = new ChessGame();
        GameData gameData = new GameData(1, "white", "black", "TestGame", game);
        dataAccess.createGame(gameData);

        GameData result = dataAccess.getGame(1);
        assertNotNull(result);
        assertEquals(1, result.gameID());
        assertEquals("TestGame", result.gameName());
    }

    @Test
    void getGameNegative() throws DataAccessException {
        GameData result = dataAccess.getGame(999);
        assertNull(result);
    }

    @Test
    void updateGamePositive() throws DataAccessException {
        ChessGame game = new ChessGame();
        GameData gameData = new GameData(1, null, null, "TestGame", game);
        dataAccess.createGame(gameData);

        GameData updated = new GameData(1, "white", "black", "TestGame", game);
        dataAccess.updateGame(updated);

        GameData result = dataAccess.getGame(1);
        assertEquals("white", result.whiteUsername());
        assertEquals("black", result.blackUsername());
    }

    @Test
    void updateGameNegative() throws DataAccessException {
        ChessGame game = new ChessGame();
        GameData gameData = new GameData(999, "white", "black", "TestGame", game);

        assertDoesNotThrow(() -> dataAccess.updateGame(gameData));
    }

    @Test
    void listGamesPositive() throws DataAccessException {
        ChessGame game1 = new ChessGame();
        ChessGame game2 = new ChessGame();
        dataAccess.createGame(new GameData(1, null, null, "Game1", game1));
        dataAccess.createGame(new GameData(2, null, null, "Game2", game2));

        var games = dataAccess.listGames();
        assertEquals(2, games.size());
    }

    @Test
    void listGamesNegative() throws DataAccessException {
        var games = dataAccess.listGames();
        assertNotNull(games);
        assertEquals(0, games.size());
    }

    @Test
    void clearGames() throws DataAccessException {
        ChessGame game = new ChessGame();
        dataAccess.createGame(new GameData(1, null, null, "TestGame", game));

        dataAccess.clearGames();

        assertEquals(0, dataAccess.listGames().size());
    }
}