package dataAccess.Memory;

import dataAccess.AuthDAO;
import dataAccess.Exceptions.DataAccessException;
import model.AuthData;

import java.util.HashMap;

public class MemAuthDao implements AuthDAO {

    private final HashMap<String, AuthData> authData = new HashMap<>();


    public AuthData createAuth(AuthData userAuth) throws DataAccessException {

        try {
            authData.put(userAuth.authToken(), userAuth);
        } catch(Exception e) {
            throw new DataAccessException("Cannot connect to server");
        }

        return userAuth;
    }

    public AuthData getAuth(String authToken) throws DataAccessException {
        AuthData userAuth;

        try {
            userAuth = authData.get(authToken);
        } catch (Exception e) {
            throw new DataAccessException("Cannot connect to server");
        }

        return userAuth;
    }

    public void deleteAuth(String authToken) throws DataAccessException {

        try {
             authData.remove(authToken);
        } catch (Exception e) {
            throw  new DataAccessException("Cannot connect to server");
        }

    }

    public void clear() throws DataAccessException {
        try {
            authData.clear();
        } catch (Exception e) {
            throw  new DataAccessException("Cannot connect to server");
        }
    }
}
