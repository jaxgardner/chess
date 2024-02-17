package dataAccess.Memory;

import dataAccess.Exceptions.DataAccessException;
import dataAccess.Exceptions.DataDoesNotExistException;
import dataAccess.UserDAO;
import model.UserData;

import java.util.HashMap;

public class MemUserDao implements UserDAO {

    private final HashMap<String, UserData> userData;

    public MemUserDao() {
        userData = new HashMap<>();
    }

    public UserData createUser(String username, String password, String email){
        UserData user = new UserData(username, password, email);
        userData.put(username, user);

        return user;
    }

    public UserData getUser(String username) throws DataAccessException {
        UserData user= userData.get(username);

        if(user == null) {
            throw new DataDoesNotExistException("User not found");
        }

        return user;
    }

    public void clear() {
        userData.clear();
    }

}

