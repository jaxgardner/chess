package service;

import chess.ChessGame;
import dataAccess.Memory.MemAuthDao;
import dataAccess.Memory.MemGameDao;
import dataAccess.Memory.MemUserDao;
import model.GameData;
import models.JoinGameRequest;

import java.util.Collection;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class GameService extends Service{
    private final MemAuthDao authDAO;
    private final MemGameDao gameDAO;

    private AtomicInteger gameCounter = new AtomicInteger(0);

    public GameService(MemUserDao userDAO, MemAuthDao authDAO, MemGameDao gameDAO) {
        super(userDAO, authDAO);
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    private Collection<GameData> getGames() throws Exception {
        return gameDAO.listGames();
    }

    private int createGameID() {
        Random random = new Random();
        String id = String.format("%04d", random.nextInt(10000));
        return  Integer.parseInt(id);
    }

    private void addGame(String gameName, int gameID) throws Exception {
        GameData newGame = new GameData(gameID, "", "", gameName, new ChessGame());

        gameDAO.addGame(newGame);
    }

    public Collection<GameData> retrieveGames(String authToken) throws Exception {
        if(verifyAuthToken(authToken)) {
            return getGames();
        }

        return null;
    }

    public Integer createGame(String authToken, String gameName) throws Exception {
        if(super.verifyAuthToken(authToken)) {
            int gameID = createGameID();
            addGame(gameName, gameID);

            return gameID;
        }

        return null;
    }

    public void joinGame(JoinGameRequest req) throws Exception {
        if(super.verifyAuthToken(req.authToken())) {
            String username = authDAO.getAuth(req.authToken()).username();
            if(req.playerColor().equals("BLACK")) {
                gameDAO.updateGameBlack(req.playerColor(), username, req.gameID());
            } else {
                gameDAO.updateGameWhite(req.playerColor(), username, req.gameID());
            }
        }
    }
}
