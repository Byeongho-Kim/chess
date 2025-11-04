package dataaccess;

import chess.ChessGame;

import model.UserData;
import model.GameData;
import model.AuthData;

import java.util.ArrayList;
import java.util.Collection;

import com.google.gson.Gson;
import org.mindrot.jbcrypt.BCrypt;
import java.sql.*;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class MySQLDataAccess implements DataAccess {
    private int nextGameID = 1;

    public MySQLDataAccess() throws DataAccessException {
        configureDatabase();
    }

    // USER

    @Override
    public void createUser(UserData user) throws DataAccessException {
        String hashedPassword = BCrypt.hashpw(user.password(), BCrypt.gensalt());
        var statement = "INSERT INTO user (username, password, email) VALUES (?, ?, ?)";
        executeUpdate(statement, user.username(), hashedPassword, user.email());
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username, password, email FROM user WHERE username=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readUser(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException("Unable to read data: " + e.getMessage());
        }
        return null;
    }

    @Override
    public void clearUsers() throws DataAccessException {
        var statement = "TRUNCATE user";
        executeUpdate(statement);
    }

    private UserData readUser(ResultSet rs) throws SQLException {
        var username = rs.getString("username");
        var password = rs.getString("password");
        var email = rs.getString("email");
        return new UserData(username, password, email);
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

    private final String[] createStatements = {
            """
        CREATE TABLE IF NOT EXISTS user (
            username VARCHAR(255) NOT NULL,
            password VARCHAR(255) NOT NULL,
            email VARCHAR(255) NOT NULL,
            PRIMARY KEY (username)
        )
        """,
            """
        CREATE TABLE IF NOT EXISTS auth (
            authToken VARCHAR(255) NOT NULL,
            username VARCHAR(255) NOT NULL,
            PRIMARY KEY (authToken)
        )
        """,
            """
        CREATE TABLE IF NOT EXISTS game (
            gameID INT NOT NULL AUTO_INCREMENT,
            whiteUsername VARCHAR(255),
            blackUsername VARCHAR(255),
            gameName VARCHAR(255) NOT NULL,
            game TEXT NOT NULL,
            PRIMARY KEY (gameID)
        )
        """
    };

    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();

        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Unable to configure database: " + ex.getMessage());
        }
    }

    private int executeUpdate(String statement, Object... params) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (var i = 0; i < params.length; i++) {
                    var param = params[i];
                    if (param instanceof String p) ps.setString(i + 1, p);
                    else if (param instanceof Integer p) ps.setInt(i + 1, p);
                    else if (param == null) ps.setNull(i + 1, NULL);
                }
                ps.executeUpdate();

                var rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
                return 0;
            }
        } catch (SQLException e) {
            throw new DataAccessException("unable to update database: " + statement + ", " + e.getMessage());
        }
    }
}
