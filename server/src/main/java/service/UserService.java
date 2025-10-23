package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;
import java.util.UUID;

public class UserService {
    private DataAccess dataAccess;

    public UserService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public RegisterResult register(String username, String password, String email) throws ServiceException {
        try {
            if (username == null || username.trim().isEmpty()) {
                throw new ServiceException("Error: bad request", 400);
            }
            if (password == null || password.trim().isEmpty()) {
                throw new ServiceException("Error: bad request", 400);
            }
            if (email == null || email.trim().isEmpty()) {
                throw new ServiceException("Error: bad request", 400);
            }
            if(dataAccess.getUser(username) != null) {
                throw new ServiceException("Error: already taken", 403);
            }

            UserData user = new UserData(username, password, email);
            dataAccess.createUser(user);

            String authToken = UUID.randomUUID().toString();
            AuthData auth = new AuthData(authToken, username);
            dataAccess.createAuth(auth);

            return new RegisterResult(username, authToken);

        }
        catch (DataAccessException e) {
            throw new ServiceException("Error: "+ e.getMessage(), 500);
        }
    }

    public LoginResult login(String username, String password) throws ServiceException {
        try {
            if (username == null || username.trim().isEmpty()) {
                throw new ServiceException("Error: bad request", 400);
            }
            if (password == null || password.trim().isEmpty()) {
                throw new ServiceException("Error: bad request", 400);
            }

            UserData user = dataAccess.getUser(username);
            if (user == null || !user.password().equals(password)) {
                throw new ServiceException("Error: unauthorized", 401);
            }

            String authToken = UUID.randomUUID().toString();
            AuthData auth = new AuthData(authToken, username);
            dataAccess.createAuth(auth);

            return new LoginResult(username, authToken);
        }
        catch (DataAccessException e) {
            throw new ServiceException("Error: "+ e.getMessage(), 500);
        }
    }

    public void logout(String authToken) throws ServiceException {
        try {
            if (dataAccess.getAuth(authToken) == null) {
                throw new ServiceException("Error: unauthorized", 401);
            }

            dataAccess.deleteAuth(authToken);
        }
        catch (DataAccessException e) {
            throw new ServiceException("Error: "+ e.getMessage(), 500);
        }
    }

    public static class RegisterResult {
        public String username;
        public String authToken;

        public RegisterResult(String username, String authToken) {
            this.username = username;
            this.authToken = authToken;
        }
    }

    public static class LoginResult {
        public String username;
        public String authToken;

        public LoginResult(String username, String authToken) {
            this.username = username;
            this.authToken = authToken;
        }
    }
}
