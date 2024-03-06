package dataAccess;

import dataAccess.Exceptions.DataAccessException;
import model.AuthData;

public interface AuthDAO {
    public AuthData createAuth(AuthData authData) throws DataAccessException;

    public AuthData getAuth(String authToken) throws DataAccessException;

    public void deleteAuth(String authToken) throws DataAccessException;

    public void clear() throws DataAccessException;

}
