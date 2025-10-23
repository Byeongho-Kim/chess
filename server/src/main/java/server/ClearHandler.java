package server;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import service.ServiceException;

public class ClearHandler {
    private DataAccess dataAccess;

    public ClearHandler(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public void clear(Context ctx) throws Exception {
        try {
            dataAccess.clearUsers();
            dataAccess.clearGames();
            dataAccess.clearAuths();
            ctx.status(200);
        }
        catch (DataAccessException e) {
            throw new ServiceException("Error: " + e.getMessage(), 500);
        }
    }
}
