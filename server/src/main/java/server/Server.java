package server;

import com.google.gson.Gson;
import dataAccess.Memory.MemAuthDao;
import dataAccess.Memory.MemGameDao;
import dataAccess.Memory.MemUserDao;
import exception.ServiceLogicException;
import model.AuthData;
import model.GameData;
import model.UserData;
import models.*;
import org.eclipse.jetty.client.HttpRequestException;
import service.GameService;
import service.LoginService;
import service.RegisterService;
import spark.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;

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
        Spark.post("/session", this::loginUser);
        Spark.delete("/session", this::logoutUser);
        Spark.get("/game", this::listGames);
        Spark.post("/game", this::createGame);
        Spark.put("/game", this::joinGame);

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

    private Object loginUser(Request req, Response res) throws ServiceLogicException {
        var userLogin = new Gson().fromJson(req.body(), LoginRequest.class);

        AuthData userAuth = loginService.getLogin(userLogin);
        if(userAuth == null) {
            res.status(401);
            return new Gson().toJson(new ErrorResponse("Error: unauthorized"));
        }

        res.status(200);
        return new Gson().toJson(userAuth);
    }

    private Object logoutUser(Request req, Response res) throws ServiceLogicException {
        String authToken = req.headers("authorization");

        if(!loginService.getLogout(authToken)) {
            res.status(401);
            return new Gson().toJson(new ErrorResponse("Error: unauthorized"));
        }

        res.status(200);
        return new Gson().toJson(new Object());
    }

    private Object createGame(Request req, Response res) throws ServiceLogicException {
        String authToken = req.headers("authorization");
        var gameInfo = new Gson().fromJson(req.body(), CreateGameRequest.class);

        if(gameInfo.gameName().isEmpty()) {
            res.status(400);
            return new Gson().toJson(new ErrorResponse("Error: bad request"));
        }

        Integer gameId = gameService.createGame(authToken, gameInfo.gameName());

        if(gameId == null) {
            res.status(401);
            return new Gson().toJson(new ErrorResponse("Error: unauthorized"));
        }

        res.status(200);
        return new Gson().toJson(new CreateGameResponse(gameId));
    }

    private Object listGames(Request req, Response res) throws ServiceLogicException {
        String authToken = req.headers("authorization");

        var games = gameService.retrieveGames(authToken);
        if(games == null) {
            res.status(401);
            return new Gson().toJson(new ErrorResponse("Error: unauthorized"));
        }


        res.status(200);
        return  new Gson().toJson(Map.of("games", games));
    }

    private Object joinGame(Request req, Response res) throws ServiceLogicException {
        String authToken = req.headers("authorization");

        JoinGameRequest gameInfo = new Gson().fromJson(req.body(), JoinGameRequest.class);

        res.status(200);
        return new Gson().toJson(new Object());
    }
}
