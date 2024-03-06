package service;

import dataAccess.AuthDAO;
import dataAccess.Exceptions.DataAccessException;
import dataAccess.Memory.MemAuthDao;
import dataAccess.Memory.MemUserDao;
import dataAccess.UserDAO;
import exception.ServiceLogicException;
import model.AuthData;
import model.UserData;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.UUID;

public class Service {
    UserDAO userDAO;
    AuthDAO authDAO;
    protected BCryptPasswordEncoder encoder;

    public Service(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
        encoder = new BCryptPasswordEncoder();

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

        return new AuthData(uuidString, username);
    }

    protected void addToAuthData(AuthData userAuth) throws ServiceLogicException {
        try {
            authDAO.createAuth(userAuth);
        } catch(DataAccessException e) {
            throw new ServiceLogicException(500, e.getMessage());
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

    protected String hashPassword (String password) {
        return encoder.encode(password);
    }

}
