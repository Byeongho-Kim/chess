package service;

import chess.ChessGame;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.AuthData;
import model.GameData;
import java.util.ArrayList;
import java.util.Collection;

public class GameService {
    private DataAccess dataAccess;

    public GameService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public ListGamesResult listGames(String authToken) throws ServiceException {
        try {
            AuthData auth = dataAccess.getAuth(authToken);
            if(auth == null) {
                throw new ServiceException("Error: unauthorized", 401);
            }

            Collection<GameData> games = dataAccess.listGames();
            return new ListGamesResult(new ArrayList<>(games));
        }
        catch (DataAccessException e) {
            throw new ServiceException("Error: " + e.getMessage(), 500);
        }
    }

    public CreateGameResult createGame(String authToken, String gameName) throws ServiceException {
        try {
            AuthData auth = dataAccess.getAuth(authToken);
            if (auth == null) {
                throw new ServiceException("Error: unauthorized", 401);
            }

            int gameID = dataAccess.getNextGameID();
            GameData game = new GameData(gameID, null, null, gameName, new ChessGame());
            dataAccess.createGame(game);

            return new CreateGameResult(gameID);
        }
        catch (DataAccessException e) {
            throw new ServiceException("Error: " + e.getMessage(), 500);
        }
    }

    public void joinGame(String authToken, String playerColor, int gameID) throws ServiceException {
        try {
            AuthData auth = dataAccess.getAuth(authToken);
            if (auth == null) {
                throw new ServiceException("Error: bad request", 401);
            }

            GameData game = dataAccess.getGame(gameID);
            if (game == null) {
                throw new ServiceException("Error: bad request", 400);
            }

            String currentPlayer = playerColor.equals("WHITE") ? game.whiteUsername() : game.blackUsername();
            if (currentPlayer != null)
                throw new ServiceException("Error: already taken", 403);

            GameData updatedGame = new GameData(game.gameID(),playerColor.equals("WHITE") ? auth.username():game.whiteUsername(), playerColor.equals("BLACK") ? auth.username():game.blackUsername(), game.gameName(), game.game());
            dataAccess.updateGame(updatedGame);
        }
        catch (DataAccessException e) {
            throw new ServiceException("Error: " + e.getMessage(), 500);
        }
    }

    public static class ListGamesResult {
        public java.util.List<GameData> games;

        public ListGamesResult(java.util.List<GameData> games) {
            this.games = games;
        }
    }

    public static class CreateGameResult {
        public int gameID;

        public CreateGameResult(int gameID) {
            this.gameID = gameID;
        }
    }
}
