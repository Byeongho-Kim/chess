package server;

import dataaccess.DataAccess;
import service.GameService;
import service.ServiceException;
import service.UserService;

import com.google.gson.Gson;
import io.javalin.http.Context;

public class GameHandler {
    private GameService gameService;
    private Gson gson = new Gson();

    public GameHandler(DataAccess dataAccess) {
        this.gameService = new GameService(dataAccess);
    }

    public void listGames(Context ctx) throws Exception {
        String authToken = ctx.header("authorization");
        GameService.ListGamesResult result = gameService.listGames(authToken);
        ctx.json(gson.toJson(result));
    }

    public void createGame(Context ctx) throws Exception {
        String authToken = ctx.header("authorization");
        CreateGameRequest request = gson.fromJson(ctx.body(), CreateGameRequest.class);
        GameService.CreateGameResult result = gameService.createGame(authToken, request.gameName);
        ctx.json(gson.toJson(result));
    }

    public void joinGame(Context ctx) throws Exception {
        String authToken = ctx.header("authorization");
        JoinGameRequest request = gson.fromJson(ctx.body(), JoinGameRequest.class);
        gameService.joinGame(authToken, request.playerColor, request.gameID);
        ctx.status(200);
    }

    public static class CreateGameRequest {
        public String gameName;
    }

    public static class JoinGameRequest {
        public String playerColor;
        public int gameID;
    }
}
