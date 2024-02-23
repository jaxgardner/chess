package service;

import dataAccess.Memory.MemAuthDao;
import dataAccess.Memory.MemUserDao;
import model.AuthData;
import model.UserData;

public class RegisterService extends Service {
    private final MemUserDao userDAO;

    public RegisterService(MemUserDao userDAO, MemAuthDao authDAO) {
        super(userDAO, authDAO);
        this.userDAO = userDAO;
    }

    private void createNewUser(UserData user) throws Exception {
        userDAO.createUser(user);
    }

    public AuthData registerUser(UserData userData) throws Exception {
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
