package server;

import io.javalin.*;
import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import io.javalin.http.Context;
import service.ServiceException;
import dataaccess.DataAccessException;

public class Server {

    private final Javalin javalin;
    private DataAccess dataAccess;
    private UserHandler userHandler;
    private GameHandler gameHandler;
    private ClearHandler clearHandler;

    public Server() {
        dataAccess = new MemoryDataAccess();
        userHandler = new UserHandler(dataAccess);
        gameHandler = new GameHandler(dataAccess);
        clearHandler = new ClearHandler(dataAccess);

        javalin = Javalin.create(config -> config.staticFiles.add("web"))
                // Register your endpoints and exception handlers here.
                .post("/user", userHandler::register)
                .post("/session", userHandler::login)
                .delete("/session", userHandler::logout)
                .get("/game", gameHandler::listGames)
                .post("/game", gameHandler::createGame)
                .put("/game", gameHandler::joinGame)
                .delete("/db", clearHandler::clear)
                .exception(ServiceException.class, this::handleServiceException)
                .exception(DataAccessException.class, this::handleDataAccessException);

    }

    private void handleServiceException(ServiceException ex, Context ctx) {
        ctx.status(ex.getStatusCode());
        ctx.json("{\"message\":\"" + ex.getMessage() + "\"}");
    }

    private void handleDataAccessException(DataAccessException ex, Context ctx) {
        ctx.status(500);
        ctx.json("{\"message\":\"Error: " + ex.getMessage() + "\"}");
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
