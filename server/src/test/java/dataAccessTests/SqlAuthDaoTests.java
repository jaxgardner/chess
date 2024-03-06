package dataAccessTests;


import dataAccess.Exceptions.DataAccessException;
import dataAccess.MySql.SqlAuthDao;
import model.AuthData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SqlAuthDaoTests {

    private static final SqlAuthDao authDAO;

    static {
        try {
            authDAO = new SqlAuthDao();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    public void testSetup() throws DataAccessException {
        authDAO.clear();
    }

    @Test
    public void createAuth() throws DataAccessException {
        authDAO.createAuth(new AuthData("q324rosadfhq43", "Jaxon"));

        AuthData userAuth = authDAO.getAuth("q324rosadfhq43");

        Assertions.assertEquals("Jaxon", userAuth.username());
    }

    @Test
    public void createAuthNullData() throws DataAccessException {
        authDAO.createAuth(new AuthData("dfasdf4r4r", ""));

        AuthData  userAuth = authDAO.getAuth("dfasdf4r4r");

        Assertions.assertNull(userAuth);
    }


    @Test
    public void getAuth() throws DataAccessException {
        authDAO.createAuth(new AuthData("q324rosadfhq43", "Jaxon"));

        AuthData userAuth = authDAO.getAuth("q324rosadfhq43");

        Assertions.assertEquals("q324rosadfhq43", userAuth.authToken());
        Assertions.assertEquals("Jaxon", userAuth.username());
    }

    @Test
    public void getNullAuth() throws DataAccessException {
        authDAO.createAuth(new AuthData("q324rosadfhq43", "Jaxon"));

        AuthData userAuth = authDAO.getAuth("q324ros");

        Assertions.assertNull(userAuth);
    }

    @Test
    public void deleteAuth() throws DataAccessException {
        authDAO.createAuth(new AuthData("q324rosadfhq43", "Jaxon"));

        authDAO.deleteAuth("q324rosadfhq43");

        AuthData userAuth =  authDAO.getAuth("q324rosadfhq43");

        Assertions.assertNull(userAuth);
    }

    @Test
    public void deleteAuthWrongToken() throws DataAccessException {
        authDAO.createAuth(new AuthData("q324rosadfhq43", "Jaxon"));

        authDAO.deleteAuth("q324rosadq43");

        AuthData userAuth =  authDAO.getAuth("q324rosadfhq43");

        Assertions.assertEquals("Jaxon", userAuth.username());
    }

    @Test
    public void clearAuth() throws DataAccessException {
        authDAO.createAuth(new AuthData("q324rosadfhq41", "Jaxon"));
        authDAO.createAuth(new AuthData("q324rosadfhq42", "Jaxon"));
        authDAO.createAuth(new AuthData("q324rosadfhq43", "Jaxon"));
        authDAO.createAuth(new AuthData("q324rosadfhq44", "Jaxon"));

        authDAO.clear();

        AuthData user1 = authDAO.getAuth("q324rosadfhq41");
        AuthData user2 = authDAO.getAuth("q324rosadfhq42");
        AuthData user3 = authDAO.getAuth("q324rosadfhq43");
        AuthData user4 = authDAO.getAuth("q324rosadfhq44");

        Assertions.assertNull(user1);
        Assertions.assertNull(user2);
        Assertions.assertNull(user3);
        Assertions.assertNull(user4);
    }

}
