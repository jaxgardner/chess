package dataAccess.Memory;

import dataAccess.AuthDAO;
import dataAccess.Exceptions.DataAccessException;
import model.AuthData;

import java.util.HashMap;

public class MemAuthDao implements AuthDAO {

    private HashMap<String, AuthData> authData;

    public MemAuthDao() {
        authData = new HashMap<>();
    }

    public AuthData createAuth(String authToken, String username) throws DataAccessException {
        AuthData newAuthData;
        newAuthData = new AuthData(authToken, username);

        try {
            authData.put(authToken, newAuthData);
        } catch(Exception e) {
            throw new DataAccessException("Cannot connect to server");
        }

        return newAuthData;
    }

    public boolean getAuth(String authToken) throws DataAccessException {
        boolean containsAuthData;

        try {
            containsAuthData = authData.containsKey(authToken);
        } catch (Exception e) {
            throw new DataAccessException("Cannot connect to server");
        }

        return containsAuthData;
    }

    public AuthData deleteAuth(String authToken) throws DataAccessException {
        AuthData deletedData;

        try {
             deletedData = authData.remove(authToken);
        } catch (Exception e) {
            throw  new DataAccessException("Cannot connect to server");
        }

        return deletedData;

    }

    public void clear() throws DataAccessException {
        try {
            authData.clear();
        } catch (Exception e) {
            throw  new DataAccessException("Cannot connect to server");
        }
    }
}
