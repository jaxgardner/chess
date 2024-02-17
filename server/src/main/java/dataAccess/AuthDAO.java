package dataAccess;

import dataAccess.Exceptions.DataAccessException;
import model.AuthData;

public interface AuthDAO {
    public AuthData createAuth(String authToken, String username) throws DataAccessException;

    public boolean getAuth(String authToken);

    public void deleteAuth(String AuthToken) throws DataAccessException;

    public void clear();

}
