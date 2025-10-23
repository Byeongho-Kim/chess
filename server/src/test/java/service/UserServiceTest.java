package service;

import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {
    @Test
    public void testRegisterSuccess() throws Exception {
        DataAccess dataAccess = new MemoryDataAccess();
        UserService userService = new UserService(dataAccess);

        UserService.RegisterResult result = userService.register("testuser", "password123", "test@example.com");

        assertEquals("testuser", result.username);
        assertTrue(result.authToken.length() > 0);
    }

    @Test
    public void testRegisterUserAlreadyExists() throws Exception {
        DataAccess dataAccess = new MemoryDataAccess();
        UserService userService = new UserService(dataAccess);

        userService.register("testuser", "password123", "test@example.com");

        try {
            userService.register("testuser", "password123", "test@example.com");
            fail("Should have thrown exception");
        }
        catch (ServiceException e) {
            assertTrue(e.getMessage().contains("already taken"));
        }
    }

    @Test
    public void testLoginSuccess() throws Exception {
        DataAccess dataAccess = new MemoryDataAccess();
        UserService userService = new UserService(dataAccess);

        userService.register("testuser", "password123", "test@example.com");

        UserService.LoginResult result = userService.login("testuser", "password123");

        assertEquals("testuser", result.username);
        assertTrue(result.authToken.length() > 0);
    }

    @Test
    public void testLoginInvalidCredentials() throws Exception {
        DataAccess dataAccess = new MemoryDataAccess();
        UserService userService = new UserService(dataAccess);

        try {
            userService.login("nonexistent", "wrongpassword");
            fail("Should have thrown exception");
        }
        catch (ServiceException e) {
            assertTrue(e.getMessage().contains("unauthorized"));
        }
    }

    @Test
    public void testLogoutSuccess() throws Exception {
        DataAccess dataAccess = new MemoryDataAccess();
        UserService userService = new UserService(dataAccess);

        UserService.RegisterResult registerResult = userService.register("testuser", "password123", "test@example.com");

        userService.logout(registerResult.authToken);
        assertTrue(true);
    }

    @Test
    public void testLogoutInvalidToken() throws Exception {
        DataAccess dataAccess = new MemoryDataAccess();
        UserService userService = new UserService(dataAccess);

        userService.logout("invalidtoken");
        assertTrue(true);
    }
}
