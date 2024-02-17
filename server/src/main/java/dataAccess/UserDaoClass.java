package dataAccess;

import model.UserData;

import java.util.HashMap;

public class UserDaoClass implements UserDAO {

    private HashMap<String, UserData> userData;

    public void UserDao() {
        userData = new HashMap<>();
    }

    public UserData createUser(String username, String password, String email){
        UserData user = new UserData(username, password, email);
        userData.put(username, user);

        return user;
    }

    public UserData getUser(String username) throws DataAccessException {
        UserData user;
        try {
            user = userData.get(username);
        } catch(NullPointerException e) {
            throw new DataAccessException("User not found");
        }

        return user;
    }
}

