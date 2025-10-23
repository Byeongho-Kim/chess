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
        RegisterRequest request = gson.fromJson(ctx.body(), RegisterRequest.class);

        if (request.username == null || request.password == null || request.email == null) {
            ctx.status(400);
            ctx.json("{\"message\":\"Error: bad request\"}");
            return;
        }

        UserService.RegisterResult result = userService.register(request.username, request.password, request.email);
        ctx.json(gson.toJson(result));
    }

    public void login(Context ctx) throws Exception {
        LoginRequest request = gson.fromJson(ctx.body(), LoginRequest.class);

        if (request.username == null || request.password == null) {
            ctx.status(400);
            ctx.json("{\"message\":\"Error: bad request\"}");
            return;
        }

        UserService.LoginResult result = userService.login(request.username, request.password);
        ctx.json(gson.toJson(result));
    }

    public void logout(Context ctx) throws Exception {
        String authToken = ctx.header("authorization");

        if (authToken == null || authToken.trim().isEmpty()) {
            ctx.status(401);
            ctx.json("{\"message\":\"Error: unauthorized\"}");
            return;
        }

        try {
            userService.logout(authToken);
            ctx.status(200);
        } catch (ServiceException e) {
            // If logout fails (e.g., invalid token), return 401
            ctx.status(401);
            ctx.json("{\"message\":\"Error: unauthorized\"}");
        }
    }

    public static class RegisterRequest {
        public String username;
        public String password;
        public String email;
    }

    public static class LoginRequest {
        public String username;
        public String password;
    }
}
