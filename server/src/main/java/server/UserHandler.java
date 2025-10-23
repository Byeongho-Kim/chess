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
        UserService.RegisterResult result = userService.register(request.username, request.password, request.email);
        ctx.json(gson.toJson(result));
    }

    public void login(Context ctx) throws Exception {
        LoginRequest request = gson.fromJson(ctx.body(), LoginRequest.class);
        UserService.LoginResult result = userService.login(request.username, request.password);
        ctx.json(gson.toJson(result));
    }

    public void logout(Context ctx) throws Exception {
        String authToken = ctx.header("authorization");
        userService.logout(authToken);
        ctx.status(200);
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
