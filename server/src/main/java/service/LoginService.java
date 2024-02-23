package service;

import dataAccess.Memory.MemAuthDao;
import dataAccess.Memory.MemUserDao;
import model.AuthData;
import model.UserData;
import models.LoginRequest;

import java.util.Objects;

public class LoginService extends Service {

    private final MemAuthDao authDAO;

    public LoginService(MemUserDao userDao, MemAuthDao authDao) {
        super(userDao, authDao);
        this.authDAO = authDao;
    }


    private boolean checkLoginPassword(LoginRequest userLogin) throws Exception {
        UserData userFromDB = getUser(userLogin.username());

        return userFromDB != null && Objects.equals(userLogin.password(), userFromDB.password());
    }



    private void deleteFromAuthData(String authToken) throws Exception {
        authDAO.deleteAuth(authToken);
    }

    public AuthData getLogin(LoginRequest req) throws Exception {
        AuthData newUserAuth = null;
        if(checkLoginPassword(req)) {
            newUserAuth = generateAuth(req.username());
            addToAuthData(newUserAuth);
        }

        return newUserAuth;
    }

    public void getLogout(String authToken) throws Exception {
        if(super.verifyAuthToken(authToken)) {
            deleteFromAuthData(authToken);
        }
    }


}
