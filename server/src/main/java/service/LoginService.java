package service;

import dataAccess.AuthDAO;
import dataAccess.Exceptions.DataAccessException;
import dataAccess.Memory.MemAuthDao;
import dataAccess.Memory.MemUserDao;
import dataAccess.UserDAO;
import exception.ServiceLogicException;
import model.AuthData;
import model.UserData;
import model.LoginRequest;

import java.util.Objects;

public class LoginService extends Service {

    private final AuthDAO authDAO;

    public LoginService(UserDAO userDao, AuthDAO authDao) {
        super(userDao, authDao);
        this.authDAO = authDao;
    }


    private boolean checkLoginPassword(LoginRequest userLogin) throws ServiceLogicException {
        UserData userFromDB = getUser(userLogin.username());

        return userFromDB != null && Objects.equals(userLogin.password(), userFromDB.password());
    }



    private void deleteFromAuthData(String authToken) throws ServiceLogicException {
        try {
            authDAO.deleteAuth(authToken);
        } catch (DataAccessException e) {
            throw new ServiceLogicException(500, "Cannot access data");
        }
    }

    public AuthData getLogin(LoginRequest req) throws ServiceLogicException {
        AuthData newUserAuth = null;
        if(checkLoginPassword(req)) {
            newUserAuth = generateAuth(req.username());
            addToAuthData(newUserAuth);
        }

        return newUserAuth;
    }

    public boolean getLogout(String authToken) throws ServiceLogicException {
        if(super.verifyAuthToken(authToken)) {
            deleteFromAuthData(authToken);
            return true;
        }
        return false;
    }


}
