package dataAccess;

import dataAccess.Exceptions.DataAccessException;
import model.GameData;

public interface GameDAO {
    public GameData createGame(String gameName, int gameID) throws DataAccessException;

    public GameData getGame();

    public GameData listGames();

    public void updateGame();

    public void clear();

}
