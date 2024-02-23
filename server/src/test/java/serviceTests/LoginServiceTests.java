package serviceTests;

import dataAccess.Memory.MemAuthDao;
import dataAccess.Memory.MemUserDao;
import model.AuthData;
import model.UserData;
import models.LoginRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.LoginService;
import service.RegisterService;

public class LoginServiceTests {
    private MemUserDao userDAO;
    private MemAuthDao authDAO;
    private RegisterService registerService;
    private LoginService loginService;

    private AuthData newUserAuth;
    @BeforeEach
    public void setup() throws Exception {
        userDAO = new MemUserDao();
        authDAO = new MemAuthDao();
        registerService = new RegisterService(userDAO, authDAO);

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

}
