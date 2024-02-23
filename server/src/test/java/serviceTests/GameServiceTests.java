package serviceTests;

import dataAccess.Exceptions.DataAccessException;
import dataAccess.Memory.MemAuthDao;
import dataAccess.Memory.MemGameDao;
import dataAccess.Memory.MemUserDao;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.GameService;

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
}
