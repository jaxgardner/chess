package dataAccess.Memory;

import dataAccess.Exceptions.DataAccessException;
import dataAccess.UserDAO;
import model.UserData;

import java.sql.SQLException;
import java.util.HashMap;

public class MemUserDao implements UserDAO {

    private final HashMap<String, UserData> userData = new HashMap<>();



    // Change to more specific exception when connecting to database
    public UserData createUser(UserData user) throws DataAccessException {

        try{
            userData.put(user.username(), user);
        } catch(Exception e) {
            throw new DataAccessException("Can't connect to database");
        }

        return user;
    }

    // Change to more specific exception when connecting to database
    public UserData getUser(String username) throws DataAccessException {
        UserData user;

        try {
            user = userData.get(username);
        } catch(Exception e) {
            throw new DataAccessException("Cannot connect to server");
        }

        return user;
    }

    public HashMap<String, UserData> getUsers() {
        return userData;
    }

    public void clear() throws DataAccessException {
        try {
            userData.clear();
        } catch( Exception e) {
            throw new DataAccessException("Cannot connect to server");
        }
    }

}

