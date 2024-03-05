package dataAccess.MySql;

import dataAccess.DatabaseManager;
import dataAccess.Exceptions.DataAccessException;
import dataAccess.UserDAO;
import model.UserData;

import java.sql.SQLException;

public class SqlUserDao implements UserDAO {

    public SqlUserDao() throws DataAccessException {
        configureDatabase();
    }

    public UserData createUser(UserData user) {
        return new UserData("Jax", "12345", "jaxon");
    }

    public UserData getUser(String username) throws DataAccessException {
        return null;
    }


    public void clear() {

    }

    private final String[] createStatements = {
        """
        CREATE TABLE IF NOT EXISTS userData (
          `UserId` int NOT NULL AUTO_INCREMENT,
          `Username` varchar(255) NOT NULL,
          `Password` varchar(255) NOT NULL,
          PRIMARY KEY (`UserId`)
        )
        """
    };

    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try(var conn = DatabaseManager.getConnection()) {
            for(var statement: createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Cannot connect to database");
        }
    }
}
