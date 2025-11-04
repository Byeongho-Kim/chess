package dataaccess;

import chess.ChessGame;

import model.UserData;
import model.GameData;
import model.AuthData;

import java.util.ArrayList;
import java.util.Collection;

public class MySQLDataAccess implements DataAccess {
    private int nextGameID = 1;

    public MySQLDataAccess() throws DataAccessException {
    }

    // USER

    @Override
    public void createUser(UserData user) throws DataAccessException {
        throw new DataAccessException("");
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        return null;
    }

    @Override
    public void clearUsers() throws DataAccessException {
    }

    // AUTH

    @Override
    public void createAuth(AuthData auth) throws DataAccessException {
        throw new DataAccessException("");
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        return null;
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
    }

    @Override
    public void clearAuths() throws DataAccessException {
    }

    // GAME
    @Override
    public void createGame(GameData game) throws DataAccessException {
        throw new DataAccessException("");
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        return null;
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        return new ArrayList<>();
    }

    @Override
    public void clearGames() throws DataAccessException {
    }

    // UTILITY

    @Override
    public int getNextGameID() {
        return nextGameID++;
    }
}
