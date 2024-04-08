package dataAccess.MySql;

import chess.ChessGame;
import com.google.gson.Gson;
import dataAccess.DatabaseManager;
import dataAccess.Exceptions.DataAccessException;
import dataAccess.GameDAO;
import model.GameData;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SqlGameDao implements GameDAO {

    public SqlGameDao() throws DataAccessException {
        configureDatabase();
    }

    private final String[] createStatement= {
        """
        CREATE TABLE IF NOT EXISTS gamedata (
            `GameID` INT(4) PRIMARY KEY ,
            `GameName` VARCHAR(255) NOT NULL,
            `WhiteUsername` VARCHAR(255),
            `BlackUsername` VARCHAR(255),
            `Chessgame` JSON
        );
        """
    };

    private void configureDatabase() throws DataAccessException {
        try(var conn = DatabaseManager.getConnection()) {
            for(var statement: createStatement) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }

    public void addGame(GameData newGame) throws DataAccessException {
        if(!(newGame.gameID() == 0 || newGame.gameName().isEmpty() || newGame.game() == null )) {
            try(var conn = DatabaseManager.getConnection()) {
                final var statement = "INSERT INTO gamedata (GameID, GameName, WhiteUsername, BlackUsername, Chessgame) VALUES (?, ?, ?, ?, ?)";
                try(var ps = conn.prepareStatement(statement)) {
                    ps.setInt(1, newGame.gameID());
                    ps.setString(2, newGame.gameName());
                    ps.setString(3, newGame.whiteUsername());
                    ps.setString(4, newGame.blackUsername());
                    var json = new Gson().toJson(newGame.game());
                    ps.setString(5, json);

                    ps.executeUpdate();
                }

            }
            catch (SQLException e) {
                throw new DataAccessException(e.getMessage());
            }
        }
    }

    private GameData readGame(ResultSet rs) throws DataAccessException{
        try {
            var gameID = rs.getInt("GameID");
            var gameName = rs.getString("GameName");
            var whiteUsername = rs.getString("WhiteUsername");
            var blackUsername = rs.getString("BlackUsername");
            var json = rs.getString("Chessgame");
            var chessGame = new Gson().fromJson(json, ChessGame.class);

            return new GameData(gameID , whiteUsername, blackUsername, gameName, chessGame);
        }
        catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    public GameData getGame(int gameID) throws DataAccessException {
        try(var conn = DatabaseManager.getConnection()) {
            final var statement = "SELECT * from gamedata WHERE GameID=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);

                try(var rs = ps.executeQuery()) {
                    if(rs.next()) {
                        return readGame(rs);
                    }
                }

            }
        }
        catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
        return null;
    }

    public void updateGameboard(int gameID, ChessGame game) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            final var statement = """
                    UPDATE gamedata
                    SET Chessgame=?
                    WHERE GameID=?;
                    """;
            try (var ps = conn.prepareStatement(statement)) {
                var chessgameString = new Gson().toJson(game);
                ps.setString(1, chessgameString);
                ps.setInt(2, gameID);

                ps.executeUpdate();
            }

        }
        catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public ArrayList<GameData> listGames() throws DataAccessException {
        var result = new ArrayList<GameData>();
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * from gamedata";
            try (var ps = conn.prepareStatement(statement)) {
                try (var rs = ps.executeQuery()) {
                    while (rs.next()) {
                        result.add(readGame(rs));
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
        return result;
    }

    public void updateGameWhite(String username, int gameID) throws DataAccessException {
        if(username != null) {
            try(var conn = DatabaseManager.getConnection()) {
                final var statement = "UPDATE gamedata SET WhiteUsername=? WHERE gameID=?";
                try(var ps = conn.prepareStatement(statement)) {
                    ps.setString(1, username);
                    ps.setInt(2, gameID);

                    ps.executeUpdate();
                }


            }
            catch (SQLException e) {
                throw new DataAccessException(e.getMessage());
            }
        }
    }

    public void updateGameBlack(String username, int gameID) throws DataAccessException {
        if(username != null) {
            try(var conn = DatabaseManager.getConnection()) {
                final var statement = "UPDATE gamedata SET BlackUsername=? WHERE gameID=?";
                try(var ps = conn.prepareStatement(statement)) {
                    ps.setString(1, username);
                    ps.setInt(2, gameID);

                    ps.executeUpdate();
                }
            }
            catch (SQLException e) {
                throw new DataAccessException(e.getMessage());
            }
        }
    }

    public void clear() throws DataAccessException {
        try(var conn = DatabaseManager.getConnection()) {
            final var statement = "TRUNCATE TABLE gamedata";
            try (var ps = conn.prepareStatement(statement)) {
                ps.executeUpdate();
            }
        }
        catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }
}
