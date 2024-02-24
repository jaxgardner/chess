package serviceTests;

import dataAccess.Exceptions.DataAccessException;
import dataAccess.Memory.MemAuthDao;
import dataAccess.Memory.MemUserDao;
import exception.ServiceLogicException;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import service.RegisterService;


public class RegisterServiceTests {

    @Test
    public void registerUser() throws ServiceLogicException {
        var userDAO = new MemUserDao();
        var authDAO = new MemAuthDao();

        var newUser = new UserData("Jaxrocs", "12345", "jaxrocs@byu.edu");

        var registerService = new RegisterService(userDAO, authDAO);

        AuthData newUserAuth = registerService.registerUser(newUser);

        try {
            Assertions.assertEquals(newUser, userDAO.getUser("Jaxrocs"));
        } catch (DataAccessException e) {
            throw new ServiceLogicException(500, "Cannot access data");
        }

        Assertions.assertNotEquals(newUserAuth, null);
    }

    @Test
    public void registerAlreadyCreatedUser() throws ServiceLogicException {
        var userDAO = new MemUserDao();
        var authDAO = new MemAuthDao();

        var newUser = new UserData("Jaxrocs", "12345", "jaxrocs@byu.edu");

        var registerService = new RegisterService(userDAO, authDAO);

       registerService.registerUser(newUser);
       AuthData authData = registerService.registerUser(newUser);

        Assertions.assertNull(authData);
    }

}


