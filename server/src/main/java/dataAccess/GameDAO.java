package dataAccess;

import model.GameData;

public interface GameDAO {
    public GameData createGame();

    public GameData getGame();

    public GameData listGames();

    public void updateGame();

}
