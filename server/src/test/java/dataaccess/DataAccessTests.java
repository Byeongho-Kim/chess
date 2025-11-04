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
}