package server;

import com.google.gson.Gson;
import dataAccess.Memory.MemAuthDao;
import dataAccess.Memory.MemGameDao;
import dataAccess.Memory.MemUserDao;
import exception.ServiceLogicException;
import model.AuthData;
import model.UserData;
import models.ErrorResponse;
import models.RegisterRequest;
import service.GameService;
import service.LoginService;
import service.RegisterService;
import spark.*;

public class Server {
    private MemUserDao userDAO;
    private MemAuthDao authDAO;
    private MemGameDao gameDAO;
    private GameService gameService;
    private LoginService loginService;
    private RegisterService registerService;

    public Server() {
        userDAO = new MemUserDao();
        authDAO = new MemAuthDao();
        gameDAO = new MemGameDao();
        gameService = new GameService(userDAO, authDAO, gameDAO);
        loginService = new LoginService(userDAO, authDAO);
        registerService = new RegisterService(userDAO, authDAO);
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");
        Spark.init();

        // Register your endpoints and handle exceptions here.

        Spark.post("/user", this::registerNewUser);

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private Object registerNewUser(Request req, Response res) throws ServiceLogicException {
        var userInfo = new Gson().fromJson(req.body(), UserData.class);

        if(userInfo.username().isEmpty() || userInfo.password().isEmpty() || userInfo.email().isEmpty()) {
            res.status(400);
            return new Gson().toJson(new ErrorResponse("Error: bad request"));
        }

        AuthData userAuth = registerService.registerUser(userInfo);
        if(userAuth != null) {
            res.status(200);
            return new Gson().toJson(userAuth);
        }

        res.status(403);
        return new Gson().toJson(new ErrorResponse("Error: already taken"));
    }
}
