package dataAccess;

import chess.ChessGame;
import dataAccess.Exceptions.DataAccessException;
import model.GameData;

import java.util.Collection;
import java.util.List;

public interface GameDAO {
    public void addGame(GameData newGame) throws DataAccessException;

    public GameData getGame(int gameID) throws DataAccessException;

    public List<GameData> listGames() throws DataAccessException;

    public void updateGameWhite(String username, int gameID) throws DataAccessException;

    public void updateGameBlack(String username, int gameID) throws DataAccessException;

    public void updateGameboard(int gameID, ChessGame game) throws DataAccessException;

    public void clear() throws DataAccessException;

}
