package serviceTests;

import dataAccess.Exceptions.DataAccessException;
import dataAccess.Memory.MemAuthDao;
import dataAccess.Memory.MemUserDao;
import exception.ServiceLogicException;
import model.AuthData;
import model.UserData;
import model.LoginRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.LoginService;
import service.RegisterService;


public class LoginServiceTests {
    private MemUserDao userDAO;
    MemAuthDao authDAO;
    private LoginService loginService;

    private AuthData newUserAuth;
    @BeforeEach
    public void setup() throws Exception {
        userDAO = new MemUserDao();
        authDAO = new MemAuthDao();
        RegisterService registerService = new RegisterService(userDAO, authDAO);

        newUserAuth = registerService.registerUser(new UserData("Jaxrocs", "12345", "jaxrocs@byu.edu"));
        loginService = new LoginService(userDAO, authDAO);
    }

    @Test
    public void loginUser() throws Exception {
        loginService.getLogin(new LoginRequest("Jaxrocs", "12345"));

        UserData user = userDAO.getUser("Jaxrocs");
        UserData mockUser = new UserData("Jaxrocs", "12345", "jaxrocs@byu.edu" );

        Assertions.assertEquals(user, mockUser);

    }

    @Test
    public void checkAuthToken() throws Exception{
        AuthData loggedInAuth = loginService.getLogin(new LoginRequest("Jaxrocs", "12345"));
        Assertions.assertNotEquals(loggedInAuth.authToken(), newUserAuth.authToken());
    }

    @Test
    public void logoutUser() throws ServiceLogicException {
        AuthData loggedInAuth = loginService.getLogin(new LoginRequest("Jaxrocs", "12345"));

        boolean loggedOut = loginService.getLogout(loggedInAuth.authToken());

        Assertions.assertTrue(loggedOut);

        AuthData userAuth;

        try {
            userAuth = authDAO.getAuth(loggedInAuth.authToken());
        } catch(DataAccessException e) {
            throw new ServiceLogicException(500, "Cannot access data");
        }

        Assertions.assertNull(userAuth);
    }


    @Test
    public void logoutInvalidUser() throws ServiceLogicException {
        boolean loggedOut = loginService.getLogout("q345r45113");

        Assertions.assertFalse(loggedOut);
    }
}
