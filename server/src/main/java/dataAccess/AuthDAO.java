package dataAccess;

import model.AuthData;

public interface AuthDAO {
    public AuthData createAuth();

    public AuthData getAuth();

    public void deleteAuth();

    public void clear();

}
