package service;

import dataAccess.Exceptions.DataAccessException;
import dataAccess.Memory.MemAuthDao;
import dataAccess.Memory.MemUserDao;
import exception.ServiceLogicException;
import model.AuthData;
import model.UserData;

import javax.xml.crypto.Data;
import java.util.UUID;

public class Service {
    MemUserDao userDAO;
    MemAuthDao authDAO;

    public Service(MemUserDao userDAO, MemAuthDao authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    protected UserData getUser(String username) throws ServiceLogicException {
        try {
            return userDAO.getUser(username);
        } catch (DataAccessException e) {
            throw new ServiceLogicException(500, "Cannot access data");
        }
    }

    protected AuthData generateAuth(String username) throws ServiceLogicException {
        UUID uuid = UUID.randomUUID();

        String uuidString = uuid.toString();

        AuthData newUserAuth = new AuthData(uuidString, username);

        try {
            return authDAO.createAuth(newUserAuth);
        } catch (DataAccessException e) {
            throw new ServiceLogicException(500, "Cannot access data");
        }
    }

    protected void addToAuthData(AuthData userAuth) throws ServiceLogicException {
        try {
            authDAO.createAuth(userAuth);
        } catch(DataAccessException e) {
            throw new ServiceLogicException(500, "Cannot access data");
        }
    }

    protected boolean verifyAuthToken(String authToken) throws ServiceLogicException {
        AuthData userAuth;
        try {
            userAuth = authDAO.getAuth(authToken);
        } catch (DataAccessException e) {
            throw new ServiceLogicException(500, "Cannot access data");
        }
        return userAuth != null;
    }

}
