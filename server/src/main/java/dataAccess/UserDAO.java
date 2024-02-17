package dataAccess;


import model.UserData;

import java.util.HashMap;
import java.util.Map;

public interface UserDAO {
    public UserData createUser(String username, String password, String email);

    public UserData getUser(String username) throws DataAccessException;

}

