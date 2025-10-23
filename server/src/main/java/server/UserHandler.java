package server;

import dataaccess.DataAccess;
import service.ServiceException;
import service.UserService;

import com.google.gson.Gson;
import io.javalin.http.Context;

public class UserHandler {
    private UserService userService;
    private Gson gson = new Gson();

    public  UserHandler(DataAccess dataAccess) {
        this.userService = new UserService(dataAccess);
    }

    public void register(Context ctx) throws Exception {
        String body = ctx.body();
        String username = extractValue(body, "username");
        String password = extractValue(body, "password");
        String email = extractValue(body, "email");

        var result = userService.register(username, password, email);
        ctx.json(gson.toJson(result));
    }

    public void login(Context ctx) throws Exception {
        String body = ctx.body();
        String username = extractValue(body, "username");
        String password = extractValue(body, "password");

        var result = userService.login(username, password);
        ctx.json(gson.toJson(result));
    }

    public void logout(Context ctx) throws Exception {
        String authToken = ctx.header("authorization");
        userService.logout(authToken);
        ctx.status(200);
    }

    private String extractValue(String json, String key) {
        String pattern = "\"" + key + "\":\"";
        int start = json.indexOf(pattern) + pattern.length();
        int end = json.indexOf("\"", start);
        return json.substring(start, end);
    }
}
