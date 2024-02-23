package serviceTests;

import dataAccess.Memory.MemAuthDao;
import dataAccess.Memory.MemUserDao;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import service.RegisterService;

public class RegisterServiceTests {

    @Test
    public void registerUser() throws Exception {
        var userDAO = new MemUserDao();
        var authDAO = new MemAuthDao();

        var newUser = new UserData("Jaxrocs", "12345", "jaxrocs@byu.edu");

        var registerService = new RegisterService(userDAO, authDAO);

        AuthData newUserAuth = registerService.registerUser(newUser);

        Assertions.assertEquals(newUser, userDAO.getUser("Jaxrocs"));
        Assertions.assertNotEquals(newUserAuth, null);
    }

    @Test
    public void registerAlreadyCreatedUser() throws Exception {
        var userDAO = new MemUserDao();
        var authDAO = new MemAuthDao();

        var newUser = new UserData("Jaxrocs", "12345", "jaxrocs@byu.edu");

        var registerService = new RegisterService(userDAO, authDAO);

       registerService.registerUser(newUser);
       AuthData authData = registerService.registerUser(newUser);

        Assertions.assertNull(authData);
    }

}


