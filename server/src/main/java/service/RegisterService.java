package service;

import dataAccess.Memory.MemAuthDao;
import dataAccess.Memory.MemUserDao;
import model.AuthData;
import model.UserData;

import java.util.Objects;
import java.util.UUID;

public class RegisterService {
    private final MemUserDao userDAO;
    private final MemAuthDao authDAO;

    public RegisterService() {
        userDAO = new MemUserDao();
        authDAO = new MemAuthDao();
    }

    private boolean verifyPassword(UserData user, String password) {
        return Objects.equals(user.password(), password);
    }

    private UserData getUser(String username) throws Exception {
        return userDAO.getUser(username);
    }

    private void createNewUser(UserData user) throws Exception {
        userDAO.createUser(user);
    }

    private AuthData generateAuth(String username) throws Exception {
        UUID uuid = UUID.randomUUID();

        String uuidString = uuid.toString();

        AuthData newUserAuth = new AuthData(username, uuidString);

        return authDAO.createAuth(newUserAuth);

    }

    public AuthData registerUser(UserData userData) throws Exception{
        UserData userFromDB = getUser(userData.username());

        if(userFromDB != null &&  verifyPassword(userFromDB, userData.password())) {
            createNewUser(userData);
            return generateAuth(userData.username());
        }

        return null;
    }

}
