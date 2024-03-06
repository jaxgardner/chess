package dataAccessTests;

import chess.ChessGame;
import dataAccess.Exceptions.DataAccessException;
import dataAccess.MySql.SqlGameDao;
import model.GameData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class SqlGameDaoTests {

    private static final SqlGameDao gameDAO;

    static {
        try {
            gameDAO = new SqlGameDao();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    public void setup() throws DataAccessException {
        gameDAO.clear();
    }

    @Test
    public void createGame() throws DataAccessException {
        gameDAO.addGame(new GameData(2412, "Jax1", "Jax2", "Game1", new ChessGame()));

        GameData game = gameDAO.getGame(2412);
        Assertions.assertNotNull(game);
    }

    @Test
    public void createGameBadData() throws DataAccessException {
        gameDAO.addGame(new GameData(2412, "Jax1", "Jax2", "Game1", null));

        GameData game = gameDAO.getGame(2412);
        Assertions.assertNull(game);
    }

    @Test
    public void getGame() throws DataAccessException {
        gameDAO.addGame(new GameData(2412, "Jax1", "Jax2", "Game1", new ChessGame()));

        GameData game = gameDAO.getGame(2412);
        Assertions.assertEquals(2412, game.gameID());
        Assertions.assertEquals("Jax1", game.whiteUsername());
        Assertions.assertEquals("Jax2", game.blackUsername());
        Assertions.assertEquals("Game1", game.gameName());
    }

    @Test
    public void getGameThatDoesNotExist() throws DataAccessException {
        GameData game = gameDAO.getGame(1312);

        Assertions.assertNull(game);
    }

    @Test
    public void listGames() throws DataAccessException {
        gameDAO.addGame(new GameData(2412, "Jax1", "Jax2", "Game1", new ChessGame()));
        gameDAO.addGame(new GameData(2413, "Jax1", "Jax2", "Game2", new ChessGame()));
        gameDAO.addGame(new GameData(2414, "Jax1", "Jax2", "Game3", new ChessGame()));
        gameDAO.addGame(new GameData(2415, "Jax1", "Jax2", "Game4", new ChessGame()));

        ArrayList<GameData> games = gameDAO.listGames();

        Assertions.assertEquals(4, games.size());
    }

    @Test
    public void listNoGames() throws DataAccessException {
        ArrayList<GameData> games = gameDAO.listGames();

        Assertions.assertEquals(0, games.size());
    }

    @Test
    public void updateGameData() throws DataAccessException {
        gameDAO.addGame(new GameData(2412, "Jax1", "Jax2", "Game1", new ChessGame()));

        gameDAO.updateGameWhite("Jaxes", 2412);
        gameDAO.updateGameBlack("Jaxon", 2412);

        GameData game = gameDAO.getGame(2412);

        Assertions.assertEquals("Jaxes", game.whiteUsername());
        Assertions.assertEquals("Jaxon", game.blackUsername());

    }

    @Test
    public void gameMoves() throws DataAccessException {
        gameDAO.addGame(new GameData(2412, "Jax1", "Jax2", "Game1", new ChessGame()));

    }

    @Test
    public void updateGameDataBadData() throws DataAccessException {
        gameDAO.addGame(new GameData(2412, "Jax1", "Jax2", "Game1", new ChessGame()));

        gameDAO.updateGameWhite("", 2412);
        gameDAO.updateGameBlack("", 2412);

        GameData game = gameDAO.getGame(2412);

        Assertions.assertEquals("Jax1", game.whiteUsername());
        Assertions.assertEquals("Jax2", game.blackUsername());

    }

}
