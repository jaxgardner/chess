package dataAccess.MySql;

import dataAccess.DatabaseManager;
import dataAccess.Exceptions.DataAccessException;
import dataAccess.UserDAO;
import model.UserData;

import javax.swing.text.Style;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class SqlUserDao implements UserDAO {

    public SqlUserDao() throws DataAccessException {
        configureDatabase();
    }

    public int createUser(UserData user) throws DataAccessException {

        try(var conn = DatabaseManager.getConnection()) {
            final var statement = "INSERT INTO userdata (Username, Password, Email) VALUES (?, ?, ?)";
            try (var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                ps.setString(1, user.username());
                ps.setString(2, user.password());
                ps.setString(3, user.email());

                var rows = ps.executeUpdate();
                if(rows > 0) {
                    try (var userId = ps.getGeneratedKeys()) {
                        if(userId.next()) {
                            return userId.getInt(1);
                        }
                    }
                }
            }
        }
        catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }

        return -1;
    }

    public UserData getUser(String username) throws DataAccessException {
        try(var conn = DatabaseManager.getConnection()) {
            final var statement = "SELECT * from userdata WHERE Username=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);

                try(var rs = ps.executeQuery()) {
                    if(rs.next()) {
                        return readUser(rs);
                    }
                }

            }
        }
        catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
        return null;
    }

    private UserData readUser(ResultSet rs) throws DataAccessException {
        try {
            var username = rs.getString("Username");
            var password = rs.getString("Password");
            var email = rs.getString("Email");
            return new UserData(username, password, email);
        }
        catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public void clear() throws DataAccessException{
        try(var conn = DatabaseManager.getConnection()) {
            final var statement = "DELETE FROM userdata";
            try (var ps = conn.prepareStatement(statement)) {
                ps.executeUpdate();
            }
        }
        catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }


    private final String[] createStatements = {
        """
        CREATE TABLE IF NOT EXISTS userdata (
          `UserID` int NOT NULL AUTO_INCREMENT,
          `Username` varchar(255) NOT NULL,
          `Password` varchar(255) NOT NULL,
          `Email` varchar(255) NOT NULL,
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
