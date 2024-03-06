package dataAccess.MySql;

import dataAccess.DatabaseManager;
import dataAccess.Exceptions.DataAccessException;
import dataAccess.GameDAO;
import model.GameData;

import java.sql.SQLException;
import java.util.List;

public class SqlGameDao implements GameDAO {

    public SqlGameDao() throws DataAccessException {
        configureDatabase();
    }

    private final String[] createStatements= {
        """
        CREATE TABLE IF NOT EXISTS gamedata (
            `GameID` INT PRIMARY KEY AUTO_INCREMENT,
            `GameName` VARCHAR(255) NOT NULL,
            `WhiteUserID` INT NOT NULL,
            `BlackUserID` INT NOT NULL,
            `Chessgame` JSON
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

    public void addGame(GameData newGame) throws DataAccessException {

    }

    public GameData getGame(int gameID) throws DataAccessException {
        return null;
    }

    public List<GameData> listGames() throws DataAccessException {
        return null;
    }

    public void updateGameWhite(String playerColor, String username, int gameID) throws DataAccessException {

    }

    public void updateGameBlack(String playerColor, String username, int gameID) throws DataAccessException {

    }

    public void clear() throws DataAccessException {

    }
}
