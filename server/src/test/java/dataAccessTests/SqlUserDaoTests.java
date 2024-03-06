package dataAccessTests;

import dataAccess.Exceptions.DataAccessException;
import dataAccess.MySql.SqlUserDao;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SqlUserDaoTests {
    private static final SqlUserDao userDAO;

    static {
        try {
            userDAO = new SqlUserDao();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    public void testSetup() throws DataAccessException {
        userDAO.clear();
    }


    // Creates user and gets back new UserID
    @Test
    public void createUser() throws DataAccessException {
        int userID = userDAO.createUser(new UserData("Jaxon", "12345", "jax"));

        Assertions.assertTrue(userID >= 0);
    }

    // Tries to create user with invalid data
    @Test
    public void createUserBadData() throws DataAccessException {
        int userID = userDAO.createUser(new UserData("", "12345", "jasdfa"));

        Assertions.assertEquals(-1, userID);
    }

    @Test
    public void getUser() throws DataAccessException {
        int userID = userDAO.createUser(new UserData("Jaxon", "12345", "jasdfa"));

        UserData user = userDAO.getUser("Jaxon");

        Assertions.assertEquals("Jaxon", user.username());
        Assertions.assertEquals("12345", user.password());
        Assertions.assertEquals("jasdfa", user.email());
        Assertions.assertTrue(userID >= 0);
    }

    @Test
    public void getNullUser() throws DataAccessException {
        userDAO.createUser(new UserData("Jaxon", "12345", "jasdfa"));

        UserData user = userDAO.getUser("");

        Assertions.assertNull(user);
    }

    @Test
    public void clearUsers() throws DataAccessException {
        userDAO.createUser(new UserData("Jaxon", "12345", "jasdfa"));
        userDAO.createUser(new UserData("Jaxon1", "12345", "jasdfa"));
        userDAO.createUser(new UserData("Jaxon2", "12345", "jasdfa"));
        userDAO.createUser(new UserData("Jaxon3", "12345", "jasdfa"));

        userDAO.clear();

        UserData user = userDAO.getUser("Jaxon");
        UserData user1 = userDAO.getUser("Jaxon1");
        UserData user2 = userDAO.getUser("Jaxon2");
        UserData user3 = userDAO.getUser("Jaxon3");

        Assertions.assertNull(user);
        Assertions.assertNull(user1);
        Assertions.assertNull(user2);
        Assertions.assertNull(user3);

    }
}
