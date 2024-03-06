package dataAccess.MySql;

import dataAccess.AuthDAO;
import dataAccess.DatabaseManager;
import dataAccess.Exceptions.DataAccessException;
import model.AuthData;
import model.UserData;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SqlAuthDao implements AuthDAO {

    public SqlAuthDao() throws DataAccessException {
        configureDatabase();
    }

    public AuthData createAuth(AuthData authData) throws DataAccessException {
        if(!(authData.authToken().isEmpty() || authData.username().isEmpty())) {
            try (var conn = DatabaseManager.getConnection()) {
                final var statement = "INSERT INTO authdata (Authtoken, Username) VALUES (?, ?)";
                try (var ps = conn.prepareStatement(statement)) {
                    ps.setString(1, authData.authToken());
                    ps.setString(2, authData.username());

                    ps.executeUpdate();
                    return authData;
                }
            } catch (SQLException e) {
                throw new DataAccessException(e.getMessage());
            }
        }
        return null;
    }

        public AuthData getAuth(String authToken) throws DataAccessException {
        try(var conn = DatabaseManager.getConnection()) {
            final var statement = "SELECT * from authdata WHERE Authtoken=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);

                try(var rs = ps.executeQuery()) {
                    if(rs.next()) {
                        return readAuthdata(rs);
                    }
                }

            }
        }
        catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
        return null;
    }

    public void deleteAuth(String authToken) throws DataAccessException {
        try(var conn = DatabaseManager.getConnection()) {
            final var statement = "DELETE FROM authdata WHERE Authtoken=?";
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, authToken);

                preparedStatement.executeUpdate();

            }
        }
        catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public void clear() throws DataAccessException {
        try(var conn = DatabaseManager.getConnection()) {
            final var statement = "TRUNCATE TABLE authdata";
            try (var ps = conn.prepareStatement(statement)) {
                ps.executeUpdate();
            }
        }
        catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    private AuthData readAuthdata(ResultSet rs) throws DataAccessException {
        try {
            var authtoken = rs.getString("Authtoken");
            var username = rs.getString("Username");
            return new AuthData(authtoken, username);
        }
        catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    private final String[] createStatements = {
        """
        CREATE TABLE IF NOT EXISTS authdata (
            `Authtoken` VARCHAR(255) PRIMARY KEY,
            `Username` varchar(255)
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
