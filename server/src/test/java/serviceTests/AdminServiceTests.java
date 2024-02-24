package serviceTests;

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
    public void clearAll() throws ServiceLogicException {
        MemUserDao userDAO = new MemUserDao();
        MemAuthDao authDAO = new MemAuthDao();
        MemGameDao gameDAO = new MemGameDao();

        var registerService = new RegisterService(userDAO, authDAO);
        var gameService = new GameService(userDAO, authDAO, gameDAO);
        var adminService = new AdminService(userDAO, authDAO, gameDAO);

        AuthData userAuth = registerService.registerUser(new UserData("Jaxrocs", "12345", "jax"));
        registerService.registerUser(new UserData("Jaxroc", "12345", "jax"));
        registerService.registerUser(new UserData("Jaxro", "12345", "jax"));
        registerService.registerUser(new UserData("Jaxr", "12345", "jax"));

        gameService.createGame(userAuth.authToken(), "G1");
        gameService.createGame(userAuth.authToken(), "G2");
        gameService.createGame(userAuth.authToken(), "G3");
        gameService.createGame(userAuth.authToken(), "G4");

        adminService.clear();

        Assertions.assertTrue(userDAO.getUsers().isEmpty());
        Assertions.assertTrue(authDAO.getAuthData().isEmpty());
        Assertions.assertTrue(gameDAO.getGameDataStorage().isEmpty());


    }
}
