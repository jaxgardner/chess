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
        try {
            newAuthData = new AuthData(authToken, username);
            authData.put(authToken, newAuthData);
        } catch(Exception e) {
            throw new CannotConnectToServerException("Cannot connect to server");
        }

        return newAuthData;
    }

    public boolean getAuth(String authToken) {
        return authData.containsKey(authToken);
    }

    public void deleteAuth(String authToken) throws DataAccessException {
        AuthData delete = authData.remove(authToken);

        if(delete == null) {
            throw new DataDoesNotExistException("Not authorized");
        }

    }

    public void clear() {
        authData.clear();
    }




}
