package service;

import dataAccess.AuthDAO;
import dataAccess.Exceptions.DataAccessException;
import dataAccess.Memory.MemAuthDao;
import dataAccess.Memory.MemUserDao;
import dataAccess.UserDAO;
import exception.ServiceLogicException;
import model.AuthData;
import model.UserData;

public class RegisterService extends Service {
    private final UserDAO userDAO;

    public RegisterService(UserDAO userDAO, AuthDAO authDAO) {
        super(userDAO, authDAO);
        this.userDAO = userDAO;
    }

    private void createNewUser(UserData user) throws ServiceLogicException {
        try {
            userDAO.createUser(user);
        } catch (DataAccessException e) {
            throw new ServiceLogicException(500, "Cannot access data");
        }
    }

    public AuthData registerUser(UserData userData) throws ServiceLogicException {
        UserData userFromDB = getUser(userData.username());

        if(userFromDB == null) {
            createNewUser(userData);
            AuthData newUserAuth = super.generateAuth(userData.username());
            super.addToAuthData(newUserAuth);
            return newUserAuth;
        }

        return null;
    }

}
