package dataAccess;

import dataAccess.Exceptions.DataAccessException;
import model.AuthData;

public interface AuthDAO {
    public AuthData createAuth(AuthData authData) throws DataAccessException;

    public boolean getAuth(String authToken) throws DataAccessException;

    public AuthData deleteAuth(String AuthToken) throws DataAccessException;

    public void clear() throws DataAccessException;

}
