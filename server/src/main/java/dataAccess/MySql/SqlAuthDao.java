package dataAccess.MySql;

import dataAccess.AuthDAO;
import dataAccess.DatabaseManager;
import dataAccess.Exceptions.DataAccessException;
import model.AuthData;

import java.sql.SQLException;

public class SqlAuthDao implements AuthDAO {

    public SqlAuthDao() throws DataAccessException {
        configureDatabase();
    }

    public AuthData createAuth(AuthData authData) throws DataAccessException {
        return null;
    }

    public AuthData getAuth(String authToken) throws DataAccessException {
        return null;
    }

    public AuthData deleteAuth(String authToken) throws DataAccessException {
        return null;
    }

    public void clear() throws DataAccessException {

    }

    private final String[] createStatements = {
        """
        CREATE TABLE IF NOT EXISTS authdata (
            `AuthToken` VARCHAR(255) PRIMARY KEY,
            `UserID` INT
        );
        """
    };

    private void configureDatabase() throws DataAccessException {
        try(var conn = DatabaseManager.getConnection()) {
            for(var statement: createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }

}
