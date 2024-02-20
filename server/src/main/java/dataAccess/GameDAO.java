package dataAccess;

import dataAccess.Exceptions.DataAccessException;
import model.GameData;

import java.util.Collection;

public interface GameDAO {
    public void addGame(GameData newGame) throws DataAccessException;

    public GameData getGame(int gameID) throws DataAccessException;

    public Collection<GameData> listGames() throws DataAccessException;

    public void updateGameWhite(String playerColor, String username, int gameID) throws DataAccessException;

    public void updateGameBlack(String playerColor, String username, int gameID) throws DataAccessException;

    public void clear() throws DataAccessException;

}
