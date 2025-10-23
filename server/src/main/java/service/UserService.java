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
            if(dataAccess.getUser(username) !=)
        }
    }

    public LoginResult login(String username, String password) throws ServiceException {}

    public void logout(String authToken) throws ServiceException {}

    public static class RegisterRequest {}

    public static class LoginRequest {}
}
