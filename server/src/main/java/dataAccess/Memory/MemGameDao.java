package dataAccess.Memory;

import dataAccess.Exceptions.DataAccessException;
import dataAccess.GameDAO;
import model.GameData;
import java.util.ArrayList;
import java.util.HashMap;

public class MemGameDao implements GameDAO {
    HashMap<Integer, GameData> gameDataStorage = new HashMap<>();


    public void addGame(GameData newGame) throws DataAccessException {
        try {
            gameDataStorage.put(newGame.gameID(), newGame);
        } catch (Exception e) {
            throw new DataAccessException("Cannot connect to server");
        }
    }

    public GameData getGame(int gameID) throws DataAccessException {
        GameData gameData;

        try {
            gameData = gameDataStorage.get(gameID);
        } catch (Exception e) {
            throw new DataAccessException("Cannot connect to server");
        }
        return gameData;
    }

    public ArrayList<GameData> listGames() throws DataAccessException {
        ArrayList<GameData> games = new ArrayList<>();
        try {
            for(int id : gameDataStorage.keySet()) {
                games.add(gameDataStorage.get(id));
            }
        } catch (Exception e) {
            throw new DataAccessException("Cannot connect to server");
        }

        return games;
    }

    public void updateGameWhite(String username, int gameID) throws DataAccessException {
        try {
            GameData game = getGame(gameID);
            GameData updatedGame = null;
            if(game != null) {
                updatedGame = new GameData(gameID, username, game.blackUsername(), game.gameName(), game.game());
            }
            gameDataStorage.put(gameID, updatedGame);
        } catch (Exception e) {
            throw new DataAccessException("Cannot connect to server");
        }
    }

    public void updateGameBlack(String username, int gameID) throws DataAccessException {
        try {
            GameData game = getGame(gameID);
            GameData updatedGame = null;
            if(game != null) {
                updatedGame = new GameData(gameID, game.whiteUsername(), username, game.gameName(), game.game());
            }
            gameDataStorage.put(gameID, updatedGame);
        } catch (Exception e) {
            throw new DataAccessException("Cannot connect to server");
        }
    }

    public void clear() throws DataAccessException {
        try {
            gameDataStorage.clear();
        } catch (Exception e) {
            throw new DataAccessException("Cannot connect to server");
        }
    }

}
