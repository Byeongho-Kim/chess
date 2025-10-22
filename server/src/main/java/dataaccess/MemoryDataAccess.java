package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MemoryDataAccess implements DataAccess {
    private final Map<String, UserData> users = new HashMap<>();
    private final Map<Integer, GameData> games = new HashMap<>();
    private final Map<String, AuthData> auths = new HashMap<>();
    private int nextGameID = 1;

    public void clearUsers() throws DataAccessException {
        users.clear();
    }

    public void createUser(UserData user) throws DataAccessException {
        if (users.containsKey(user.username())) {
            throw new DataAccessException("User already exists");
        }
        users.put(user.username(), user);
    }

    public UserData getUser(String username) throws DataAccessException {
        return users.get(username);
    }

    public void clearGames() throws DataAccessException {
        games.clear();
        nextGameID = 1;
    }

    public void createGame(GameData game) throws DataAccessException {
        games.put(game.gameID(), game);
    }

    public GameData getGame(int gameID) throws DataAccessException {
        return games.get(gameID);
    }

    public Collection<GameData> listGames() throws DataAccessException {
        return games.values();
    }

    public void updateGame(GameData game) throws DataAccessException {
        if (!games.containsKey(game.gameID())) {
            throw new DataAccessException("Game not found");
        }
        games.put(game.gameID(), game);
    }

    public  int getNextGameID() {
        return nextGameID++;
    }

    public void clearAuths() throws DataAccessException {
        auths.clear();
    }

    public void createAuth(AuthData auth) throws DataAccessException {
        auths.put(auth.authToken(), auth);
    }

    public AuthData getAuth(String authToken) throws DataAccessException {
        return auths.get(authToken);
    }

    public void deleteAuth(String authToken) throws DataAccessException {
        auths.remove(authToken);
    }
}
