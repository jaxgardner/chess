package serviceTests;

import dataAccess.Exceptions.DataAccessException;
import dataAccess.Memory.MemAuthDao;
import dataAccess.Memory.MemGameDao;
import dataAccess.Memory.MemUserDao;
import exception.ServiceLogicException;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import service.AdminService;
import service.GameService;
import service.RegisterService;

public class AdminServiceTests {

    @Test
    public void clearAll() throws ServiceLogicException, DataAccessException {
        var userDAO = new MemUserDao();
        var authDAO = new MemAuthDao();
        var gameDAO = new MemGameDao();

        var registerService = new RegisterService(userDAO, authDAO);
        var gameService = new GameService(userDAO, authDAO, gameDAO);
        var adminService = new AdminService(userDAO, authDAO, gameDAO);

        AuthData userAuth = registerService.registerUser(new UserData("Jaxrocs", "12345", "jax"));

        gameService.createGame(userAuth.authToken(), "G1");

        adminService.clear();

        Assertions.assertNull(userDAO.getUser("Jaxrocs"));
        Assertions.assertNull(authDAO.getAuth(userAuth.authToken()));
        Assertions.assertTrue(gameDAO.listGames().isEmpty());
    }
}
