package service;

import chess.ChessGame;
import dataAccess.Memory.MemAuthDao;
import dataAccess.Memory.MemGameDao;
import model.AuthData;
import model.GameData;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class GameService {
    private MemAuthDao authDAO;
    private MemGameDao gameDAO;

    private static final AtomicInteger gameCounter = new AtomicInteger(0);

    public GameService() {
        authDAO = new MemAuthDao();
        gameDAO = new MemGameDao();
    }

    private boolean verifyAuth(String authToken) throws Exception {
        return authDAO.getAuth(authToken);
    }

    private Collection<GameData> getGames() throws Exception {
        return gameDAO.listGames();
    }

    private int createGameID() {
        int MAX_VALUE = 9999;
        return gameCounter.getAndIncrement() % (MAX_VALUE + 1);
    }

    private void addGame(String gameName, int gameID) throws Exception {
        GameData newGame = new GameData(gameID, "", "", gameName, new ChessGame());

        gameDAO.addGame(newGame);
    }


    public Collection<GameData> retrieveGames(String authToken) throws Exception {
        if(verifyAuth(authToken)) {
            return getGames();
        }

        return null;
    }

    public Integer createGame(String authToken, String gameName) throws Exception {
        if(verifyAuth(authToken)) {
            int gameID = createGameID();
            addGame(gameName, gameID);

            return gameID;
        }

        return null;
    }



}
