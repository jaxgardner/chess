package service;

import dataAccess.Memory.MemAuthDao;
import dataAccess.Memory.MemUserDao;
import model.AuthData;
import model.UserData;

import java.util.UUID;

public class Service {
    MemUserDao userDAO;
    MemAuthDao authDAO;

    public Service(MemUserDao userDAO, MemAuthDao authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    protected UserData getUser(String username) throws Exception {
        return userDAO.getUser(username);
    }

    protected AuthData generateAuth(String username) throws Exception {
        UUID uuid = UUID.randomUUID();

        String uuidString = uuid.toString();

        AuthData newUserAuth = new AuthData(uuidString, username);

        return authDAO.createAuth(newUserAuth);
    }

    protected void addToAuthData(AuthData userAuth) throws Exception {
        authDAO.createAuth(userAuth);
    }

    protected boolean verifyAuthToken(String authToken) throws Exception {
        AuthData userAuth = authDAO.getAuth(authToken);
        return userAuth != null;
    }

}
