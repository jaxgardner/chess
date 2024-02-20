package dataAccess.Memory;

import chess.ChessGame;
import dataAccess.Exceptions.DataAccessException;
import dataAccess.GameDAO;
import model.GameData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MemGameDao implements GameDAO {
    HashMap<Integer, GameData> gameDataStorage = new HashMap<>();


    public void addGame(String gameName, int gameID) throws DataAccessException {
        GameData newGameData = new GameData(gameID, "", "", gameName, new ChessGame());
        try {
            gameDataStorage.put(gameID, newGameData);
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

    public Collection<GameData> listGames() throws DataAccessException {
        Collection<GameData> games;
        try {
            games = gameDataStorage.values();
        } catch (Exception e) {
            throw new DataAccessException("Cannot connect to server");
        }

        return games;
    }

    public void updateGameWhite(String playerColor, String username, int gameID) throws DataAccessException {
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

    public void updateGameBlack(String playerColor, String username, int gameID) throws DataAccessException {
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
