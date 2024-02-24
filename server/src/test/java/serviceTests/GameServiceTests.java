package serviceTests;

import dataAccess.Exceptions.DataAccessException;
import dataAccess.Memory.MemAuthDao;
import dataAccess.Memory.MemGameDao;
import dataAccess.Memory.MemUserDao;
import exception.ServiceLogicException;
import model.AuthData;
import model.GameData;
import model.UserData;
import models.GameListResult;
import models.JoinGameRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.GameService;

import java.util.Collection;
import java.util.List;

public class GameServiceTests {
    private MemUserDao userDAO;
    private MemAuthDao authDAO;
    private MemGameDao gameDAO;
    private GameService gameService;
    private AuthData userAuth;

    @BeforeEach
    public void setup() throws DataAccessException {
        userDAO = new MemUserDao();
        authDAO = new MemAuthDao();
        gameDAO = new MemGameDao();

        gameService = new GameService(userDAO, authDAO, gameDAO);

        userDAO.createUser(new UserData("Jaxrocs", "12345", "jaxrocs@byu.edu"));
        userAuth = authDAO.createAuth(new AuthData("145df134514rf314", "Jaxrocs"));

    }

    @Test
    public void createAGame() throws Exception {
        Integer gameID = gameService.createGame(userAuth.authToken(), "New Game");

        Assertions.assertTrue(gameID > 0);
    }

    @Test
    public void createAGameUnauthorized() throws Exception {
        Integer gameID = gameService.createGame("asdfasdfasdfa", "New Game");

        Assertions.assertNull(gameID);
    }

    @Test
    public void getGames() throws Exception {
        gameService.createGame(userAuth.authToken(), "New Game");
        gameService.createGame(userAuth.authToken(), "New Game1");
        gameService.createGame(userAuth.authToken(), "New Game2");
        gameService.createGame(userAuth.authToken(), "New Game3");

        List<GameListResult> games = gameService.retrieveGames(userAuth.authToken());

        Assertions.assertEquals(games.size(), 4);
    }

    @Test
    public void getGamesUnauthorized() throws Exception {
        List<GameListResult> games = gameService.retrieveGames("Fasdfasdfasdf");

        Assertions.assertNull(games);
    }

    @Test
    public void joinGame() throws ServiceLogicException, DataAccessException {
        GameData game;
        Integer gameID = gameService.createGame(userAuth.authToken(), "New Game");

        gameService.joinGame(userAuth.authToken(), new JoinGameRequest("BLACK", gameID));

        gameService.joinGame(userAuth.authToken(), new JoinGameRequest("WHITE", gameID));

        try {
            game = gameDAO.getGame(gameID);
        } catch (DataAccessException e) {
            throw new ServiceLogicException(500, "Data cannot be accessed");
        }

        Assertions.assertEquals(game.blackUsername(), "Jaxrocs");
        Assertions.assertEquals(game.whiteUsername(), "Jaxrocs");
    }

    @Test
    public void joinGameWrongID() throws ServiceLogicException {
        gameService.createGame(userAuth.authToken(), "New Game");

        Assertions.assertThrows(ServiceLogicException.class, () -> gameService.joinGame(userAuth.authToken(), new JoinGameRequest("WHITE", 2344)));
    }


}
