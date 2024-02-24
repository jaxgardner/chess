package service;

import chess.ChessGame;
import dataAccess.Exceptions.DataAccessException;
import dataAccess.Memory.MemAuthDao;
import dataAccess.Memory.MemGameDao;
import dataAccess.Memory.MemUserDao;
import exception.ServiceLogicException;
import model.GameData;
import models.GameListResult;
import models.JoinGameRequest;

import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class GameService extends Service{
    private final MemAuthDao authDAO;
    private final MemGameDao gameDAO;

    public GameService(MemUserDao userDAO, MemAuthDao authDAO, MemGameDao gameDAO) {
        super(userDAO, authDAO);
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    private List<GameListResult> getGames() throws ServiceLogicException {
        List<GameData> gameData;
        try {
            gameData = gameDAO.listGames();
        } catch (DataAccessException e) {
            throw new ServiceLogicException(500, "Cannot access data");
        }

        return gameData.stream()
                .map(original -> new GameListResult(original.gameID(), original.whiteUsername(), original.blackUsername(), original.gameName()))
                .toList();
    }

    private int createGameID() {
        Random random = new Random();
        String id = String.format("%04d", random.nextInt(10000));
        return  Integer.parseInt(id);
    }

    private void addGame(String gameName, int gameID) throws ServiceLogicException {
        GameData newGame = new GameData(gameID, "", "", gameName, new ChessGame());
        try {
            gameDAO.addGame(newGame);
        } catch (DataAccessException e) {
            throw new ServiceLogicException(500, "Cannot access data");
        }
    }

    public List<GameListResult> retrieveGames(String authToken) throws ServiceLogicException {
        if(verifyAuthToken(authToken)) {
            return getGames();
        }
        return null;
    }

    public Integer createGame(String authToken, String gameName) throws ServiceLogicException {
        if(super.verifyAuthToken(authToken)) {
            int gameID = createGameID();
            addGame(gameName, gameID);

            return gameID;
        }

        return null;
    }

    public boolean joinGame(String authToken, JoinGameRequest req) throws ServiceLogicException, DataAccessException {
        try {
            if(super.verifyAuthToken(authToken)) {
                String username = authDAO.getAuth(authToken).username();
                GameData game = gameDAO.getGame(req.gameID());
                if(game != null) {
                    if(req.playerColor().equals("BLACK")) {
                        gameDAO.updateGameBlack(req.playerColor(), username, req.gameID());
                    } else {
                        gameDAO.updateGameWhite(req.playerColor(), username, req.gameID());
                    }
                    return true;
                } else {
                    throw new ServiceLogicException(400, "Error: bad request");
                }
            }
        } catch (DataAccessException e) {
            throw new DataAccessException("Cannot connect to data");
        }
        return false;
    }
}
