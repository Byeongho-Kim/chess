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
        var result = gameService.listGames(authToken);
        ctx.json(gson.toJson(result));
    }

    public void createGames(Context ctx) throws Exception {
        String authToken = ctx.header("authorization");
        String body = ctx.body();
        String gameName = extractValue(body, "gameName");

        var result = gameService.createGame(authToken, gameName);
        ctx.json(gson.toJson(result));
    }

    public void joinGames(Context ctx) throws Exception {
        String authToken = ctx.header("authorization");
        String body = ctx.body();
        String playerColor = extractValue(body, "playerColor");
        String gameIDStr = extractValue(body, "gameID");
        int gameID = Integer.parseInt(gameIDStr);

        gameService.joinGame(authToken, playerColor, gameID);
        ctx.status(200);
    }

    private String extractValue(String json, String key) {
        String pattern = "\"" + key + "\":\"";
        int start = json.indexOf(pattern) + pattern.length();
        int end = json.indexOf("\"", start);
        return json.substring(start, end);
    }
}
