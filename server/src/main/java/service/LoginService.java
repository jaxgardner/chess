package service;

import dataAccess.Memory.MemAuthDao;
import dataAccess.Memory.MemUserDao;
import model.AuthData;
import model.UserData;
import models.LoginRequest;

import java.util.Objects;
import java.util.UUID;

public class LoginService {

    private final MemUserDao userDAO;
    private final MemAuthDao authDAO;

    public LoginService() {
        userDAO = new MemUserDao();
        authDAO = new MemAuthDao();
    }

    private UserData getUser(String username) throws Exception {
        return userDAO.getUser(username);
    }

    private boolean checkLoginPassword(LoginRequest userLogin) throws Exception {
        UserData userFromDB = getUser(userLogin.username());

        return userFromDB != null && Objects.equals(userLogin.password(), userFromDB.password());
    }

    private boolean checkAuthToken(AuthData req) throws Exception {
        return authDAO.getAuth(req.authToken());
    }

    private AuthData generateAuth(String username) throws Exception {
        UUID uuid = UUID.randomUUID();

        String uuidString = uuid.toString();

        AuthData newUserAuth = new AuthData(username, uuidString);

        return authDAO.createAuth(newUserAuth);
    }

    private void addToAuthData(AuthData userAuth) throws Exception {
        authDAO.createAuth(userAuth);
    }

    private void deleteFromAuthData(AuthData req) throws Exception {
        authDAO.deleteAuth(req.authToken());
    }

    public AuthData getLogin(LoginRequest req) throws Exception {
        AuthData newUserAuth = null;
        if(checkLoginPassword(req)) {
            newUserAuth = generateAuth(req.username());
            addToAuthData(newUserAuth);
        }

        return newUserAuth;
    }

    public void getLogout(AuthData req) throws Exception {
        if(checkAuthToken(req)) {
            deleteFromAuthData(req);
        }
    }


}
