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

public class RegisterService extends Service {
    private final UserDAO userDAO;

    public RegisterService(UserDAO userDAO, AuthDAO authDAO) {
        super(userDAO, authDAO);
        this.userDAO = userDAO;
    }


    private int createNewUser(UserData user) throws ServiceLogicException {
        try {
            String hashedPassword = hashPassword(user.password());
            var alteredUser = new UserData(user.username(), hashedPassword, user.email());
            return userDAO.createUser(alteredUser);
        } catch (DataAccessException e) {
            throw new ServiceLogicException(500, "Cannot access data");
        }
    }

    public AuthData registerUser(UserData userData) throws ServiceLogicException {
        UserData userFromDB = getUser(userData.username());

        if(userFromDB == null) {
            int userId = createNewUser(userData);
            if(userId >= 0) {
                AuthData newUserAuth = super.generateAuth(userData.username());
                super.addToAuthData(newUserAuth);
                return newUserAuth;
            }
        }

        return null;
    }

}
