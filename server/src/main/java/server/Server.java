package server;

import com.google.gson.Gson;
import dataAccess.Exceptions.DataAccessException;
import dataAccess.Memory.MemAuthDao;
import dataAccess.Memory.MemGameDao;
import dataAccess.Memory.MemUserDao;
import dataAccess.MySql.SqlAuthDao;
import dataAccess.MySql.SqlGameDao;
import dataAccess.MySql.SqlUserDao;
import exception.ServiceLogicException;
import model.*;
import service.AdminService;
import service.GameService;
import service.LoginService;
import service.RegisterService;
import spark.*;


import java.util.Map;

public class Server {
    private final GameService gameService;
    private final LoginService loginService;
    private final RegisterService registerService;
    private final AdminService adminService;

    public Server() throws DataAccessException {
        var userDAO = new SqlUserDao();
        var authDAO = new SqlAuthDao();
        var gameDAO = new SqlGameDao();
        gameService = new GameService(userDAO, authDAO, gameDAO);
        loginService = new LoginService(userDAO, authDAO);
        registerService = new RegisterService(userDAO, authDAO);
        adminService = new AdminService(userDAO, authDAO, gameDAO);
        userDAO.createUser(new UserData("Jaxon", "12345", "jax"));
        userDAO.getUser("Jaxon");
        userDAO.clear();
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
        Spark.delete("/db", this::clearAll);
        Spark.exception(ServiceLogicException.class, this::exceptionHandler);


        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private void exceptionHandler(ServiceLogicException ex, Request req, Response res) {
        res.status(ex.statusCode());
    }

    private Object registerNewUser(Request req, Response res) throws ServiceLogicException {
        var userInfo = new Gson().fromJson(req.body(), UserData.class);

        if(userInfo.username() == null || userInfo.password() == null || userInfo.email() == null) {
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

        if(gameInfo.gameName() == null) {
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

    private Object joinGame(Request req, Response res) throws DataAccessException {
        String authToken = req.headers("authorization");

        JoinGameRequest gameInfo = new Gson().fromJson(req.body(), JoinGameRequest.class);
        try {
            if(!gameService.joinGame(authToken, gameInfo)) {
                res.status(401);
                return new Gson().toJson(new ErrorResponse("Error: unauthorized"));
            }
        } catch (ServiceLogicException e) {
            res.status(e.statusCode());
            return new Gson().toJson(new ErrorResponse(e.getMessage()));
        }

        res.status(200);
        return new Gson().toJson(new Object());
    }

    private Object clearAll(Request req, Response res) throws ServiceLogicException {
        adminService.clear();
        res.status(200);
        return new Gson().toJson(new Object());
    }
}
