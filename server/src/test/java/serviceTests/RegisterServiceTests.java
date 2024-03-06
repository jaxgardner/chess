package serviceTests;

import dataAccess.Exceptions.DataAccessException;
import dataAccess.Memory.MemAuthDao;
import dataAccess.Memory.MemUserDao;
import dataAccess.MySql.SqlAuthDao;
import dataAccess.MySql.SqlUserDao;
import exception.ServiceLogicException;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import service.RegisterService;


public class RegisterServiceTests {

    @Test
    public void registerUser() throws ServiceLogicException, DataAccessException {
        var userDAO = new SqlUserDao();
        var authDAO = new SqlAuthDao();
        userDAO.clear();
        authDAO.clear();

        var newUser = new UserData("Jaxrocs", "12345", "jaxrocs@byu.edu");

        var registerService = new RegisterService(userDAO, authDAO);

        AuthData newUserAuth = registerService.registerUser(newUser);
        UserData user = userDAO.getUser("Jaxrocs");


        Assertions.assertEquals(newUser.email(), user.email());

        Assertions.assertNotEquals(newUserAuth, null);
    }

    @Test
    public void registerAlreadyCreatedUser() throws ServiceLogicException, DataAccessException {
        var userDAO = new SqlUserDao();
        var authDAO = new SqlAuthDao();

        var newUser = new UserData("Jaxrocs", "12345", "jaxrocs@byu.edu");

        var registerService = new RegisterService(userDAO, authDAO);

       registerService.registerUser(newUser);
       AuthData authData = registerService.registerUser(newUser);

        Assertions.assertNull(authData);
    }

}


