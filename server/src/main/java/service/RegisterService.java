package service;

import dataAccess.Memory.MemAuthDao;
import dataAccess.Memory.MemUserDao;
import model.AuthData;
import model.UserData;

import java.util.Objects;

public class RegisterService extends Service {
    private final MemUserDao userDAO;

    public RegisterService(MemUserDao userDAO, MemAuthDao authDAO) {
        super(userDAO, authDAO);
        this.userDAO = userDAO;
    }

    private boolean verifyPassword(UserData user, String password) {
        return Objects.equals(user.password(), password);
    }

    private void createNewUser(UserData user) throws Exception {
        userDAO.createUser(user);
    }

    public AuthData registerUser(UserData userData) throws Exception{
        UserData userFromDB = getUser(userData.username());

        if(userFromDB != null &&  verifyPassword(userFromDB, userData.password())) {
            createNewUser(userData);
            AuthData newUserAuth = generateAuth(userData.username());
            addToAuthData(newUserAuth);
            return newUserAuth;
        }

        return null;
    }

}
