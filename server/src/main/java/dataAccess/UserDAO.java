package dataAccess;


import dataAccess.Exceptions.DataAccessException;
import model.UserData;

public interface UserDAO {
    public int createUser(UserData user) throws DataAccessException;

    public UserData getUser(String username) throws DataAccessException;

    public void clear() throws DataAccessException;
}

