package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataAccess.AuthDAO;
import dataAccess.Exceptions.DataAccessException;
import dataAccess.GameDAO;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import webSocketMessages.serverMessages.ErrorMessage;
import webSocketMessages.serverMessages.LoadGameMessage;
import webSocketMessages.serverMessages.NotificationMessage;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.*;

import java.io.IOException;
@WebSocket
public class WebsocketHandler {
    private final ConnectionManager connections = new ConnectionManager();
    private final GameDAO sqlGameDao;
    private final AuthDAO sqlAuthDao;

    public WebsocketHandler(GameDAO sqlGameDao, AuthDAO sqlAuthDao) {
        this.sqlGameDao =  sqlGameDao;
        this.sqlAuthDao = sqlAuthDao;
    }
    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        UserGameCommand action = new Gson().fromJson(message, UserGameCommand.class);

        action = switch (action.getCommandType()) {
            case JOIN_PLAYER -> new Gson().fromJson(message, JoinPlayerCommand.class);
            case JOIN_OBSERVER -> new Gson().fromJson(message, JoinObserverCommand.class);
            case MAKE_MOVE -> new Gson().fromJson(message, MakeMoveCommand.class);
            case LEAVE -> new Gson().fromJson(message, LeaveCommand.class);
            case RESIGN -> new Gson().fromJson(message, ResignCommand.class);
        };

        switch (action.getCommandType()) {
            case JOIN_PLAYER -> joinPlayer((JoinPlayerCommand) action, session);
            case JOIN_OBSERVER -> joinObserver((JoinObserverCommand) action, session);
            case MAKE_MOVE -> makeMove((MakeMoveCommand) action);
            case LEAVE -> leave((LeaveCommand) action);
            case RESIGN -> resign((ResignCommand) action);
        }
    }

    private void joinPlayer(JoinPlayerCommand command, Session session) {
        AuthData auth = verifyAuthToken(command.getAuthString());
        String username;
        if(auth != null) {
            try {
                username = auth.username();
                GameData game = sqlGameDao.getGame(command.getGameID());
                if(game != null) {
                    if ((game.blackUsername() != null && game.blackUsername().equals(username) && command.getTeamColor() == ChessGame.TeamColor.BLACK) ||
                            (game.whiteUsername() != null && game.whiteUsername().equals(username) && command.getTeamColor() == ChessGame.TeamColor.WHITE)) {
                        connections.add(game.gameID(), username, session);
                        LoadGameMessage gameMessage = createLoadGameMessage(game);
                        connections.sendToOneUser(game.gameID(), username, gameMessage);
                        NotificationMessage joinGameBroadcast = getJoinBroadcastMessage(username, command.getTeamColor());
                        connections.broadcast(game.gameID(), username, joinGameBroadcast );
                    } else {
                        sendBackErrorMessage(session, "Error: cannot join team");
                    }
                } else {
                    sendBackErrorMessage(session, "Error: game does not exist");
                }
            } catch (DataAccessException | IOException e) {
                // Switch for error message
                System.out.println("Unable to get game");
            }
        } else {
           sendBackErrorMessage(session, "Error: bad auth token");
        }

    }

    private void sendBackErrorMessage(Session session, String message) {
        ErrorMessage errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, message);
        String serverMessage = new Gson().toJson(errorMessage);
        try {
            session.getRemote().sendString(serverMessage);
        } catch (IOException e) {
            System.out.println("Unable to connect");
        }
    }

    private NotificationMessage getJoinBroadcastMessage(String username, ChessGame.TeamColor teamColor) {
        String color;
        if(teamColor == ChessGame.TeamColor.WHITE) {
            color = "white";
        } else {
            color = "black";
        }
        String message = username + " has joined the game as player " + color;
        return new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
    }

    private void joinObserver(JoinObserverCommand command, Session session) {
        AuthData auth = verifyAuthToken(command.getAuthString());
        String username;
        if(auth != null) {
            try {
                username = auth.username();
                GameData game = sqlGameDao.getGame(command.getGameID());
                if(game != null) {
                    connections.add(game.gameID(), username, session);
                    LoadGameMessage gameMessage = createLoadGameMessage(game);
                    connections.sendToOneUser(game.gameID(), username, gameMessage);
                    NotificationMessage notificationMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, username + "has joined game as observer" );
                    connections.broadcast(game.gameID(), username, notificationMessage );
                } else {
                    ErrorMessage errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Error: game does not exist");
                    String message = new Gson().toJson(errorMessage);
                    try {
                        session.getRemote().sendString(message);
                    } catch (IOException e) {
                        System.out.println("Unable to connect");
                    }

                }
            } catch (DataAccessException | IOException e) {
                System.out.println("Server error");
            }
        } else {
            ErrorMessage errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Error: bad auth");
            String message = new Gson().toJson(errorMessage);
            try {
                session.getRemote().sendString(message);
            } catch (IOException e) {
                System.out.println("Unable to connect");
            }
        }
    }

    private void leave(LeaveCommand command) throws IOException {
        AuthData auth = verifyAuthToken(command.getAuthString());
        String username;
        if(auth != null) {
            username = auth.username();
            NotificationMessage leftGameMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, username + " has left the game");
            connections.broadcast(command.getGameID(), "", leftGameMessage );
            connections.remove(command.getGameID(), username);
            try {
                GameData game = sqlGameDao.getGame(command.getGameID());
                if(game.whiteUsername() != null && game.whiteUsername().equals(username)) {
                    sqlGameDao.updateGameWhite("", game.gameID());
                } else if(game.blackUsername() != null && game.blackUsername().equals(username)) {
                    sqlGameDao.updateGameBlack("", game.gameID());
                }
            } catch (DataAccessException e) {
                System.out.println("Unable to access game");
            }
        }
    }

    private void makeMove(MakeMoveCommand command) {
        AuthData auth = verifyAuthToken(command.getAuthString());
        String username;
        if(auth != null) {
            username = auth.username();
            try {
                GameData gameData = sqlGameDao.getGame(command.getGameID());
                ChessGame game = gameData.game();
                if(!game.isOver()) {
                    if (correctTurn(username, gameData)) {
                        ServerMessage message = evaluateMove(command.getGameID(), game, command.getChessMove());
                        connections.sendToOneUser(command.getGameID(), username, message);
                        if (message.getServerMessageType() != ServerMessage.ServerMessageType.ERROR) {
                            connections.broadcast(command.getGameID(), username, message);
                            NotificationMessage makeMoveMessage = getMakeMoveMessage(username, command.getChessMove());
                            connections.broadcast(command.getGameID(), username, makeMoveMessage);
                        }

                        String checkGameUsername = isInCheck(gameData, username);

                        if (checkGameUsername != null) {
                            NotificationMessage message1 = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, checkGameUsername + " is in check");
                            connections.broadcast(command.getGameID(), "", message1);
                        }
                        checkGameUsername = isInCheckmate(gameData, username);
                        if (checkGameUsername != null) {
                            NotificationMessage message1 = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, checkGameUsername + " is in checkmate");
                            connections.broadcast(command.getGameID(), "", message1);
                            game.setOver(true);
                            sqlGameDao.updateGameboard(command.getGameID(), game);
                        }
                    } else {
                        ErrorMessage errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Not your turn");
                        connections.sendToOneUser(command.getGameID(), username, errorMessage);
                    }
                } else {
                    ErrorMessage errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Game is over");
                    connections.sendToOneUser(command.getGameID(), username, errorMessage);
                }
            } catch (DataAccessException | IOException e){
                System.out.println("Server Error");
            }
        }
    }

    private void resign(ResignCommand command) {
        AuthData auth = verifyAuthToken(command.getAuthString());
        String username;
        if(auth != null) {
            username = auth.username();
            try {
                GameData gameData = sqlGameDao.getGame(command.getGameID());
                if(gameData.blackUsername().equals(username) || gameData.whiteUsername().equals(username)) {
                    ChessGame game = gameData.game();
                    if(!game.isOver())  {
                        game.setOver(true);
                        sqlGameDao.updateGameboard(command.getGameID(), game);
                        NotificationMessage resignNotification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, username + " has resigned from the game");
                        connections.broadcast(command.getGameID(), "", resignNotification);
                    } else {
                        ErrorMessage errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Game is already over");
                        connections.sendToOneUser(command.getGameID(), username, errorMessage);
                    }
                } else {
                    ErrorMessage errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Not authorized to resign from game");
                    connections.sendToOneUser(command.getGameID(), username, errorMessage);
                }
            }  catch (DataAccessException | IOException e){
                System.out.println("Server Error");
            }
        }
    }

    private String isInCheck(GameData gameData, String username) {
        String whiteUsername = gameData.whiteUsername();
        String blackUsername = gameData.blackUsername();

        if(whiteUsername.equals(username) && gameData.game().isInCheck(ChessGame.TeamColor.BLACK) ) {
            return blackUsername;
        } else if(blackUsername.equals(username) && gameData.game().isInCheck(ChessGame.TeamColor.WHITE)) {
            return whiteUsername;
        }
        return null;
    }

    private String isInCheckmate(GameData gameData, String username) {
        String whiteUsername = gameData.whiteUsername();
        String blackUsername = gameData.blackUsername();

        if(whiteUsername.equals(username) && gameData.game().isInCheckmate(ChessGame.TeamColor.BLACK)) {
            return blackUsername;
        } else if(blackUsername.equals(username) && gameData.game().isInCheckmate(ChessGame.TeamColor.WHITE)) {
            return whiteUsername;
        }
        return null;
    }

    private NotificationMessage getMakeMoveMessage(String username, ChessMove chessMove) {
        String message = username + " moved piece from " + chessMove.toString();
        return new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
    }

    private boolean correctTurn(String username, GameData gameData) {
        ChessGame.TeamColor currentColor = gameData.game().getTeamTurn();

        if(gameData.blackUsername() != null && gameData.blackUsername().equals(username) && currentColor == ChessGame.TeamColor.BLACK) {
            return true;
        } else if(gameData.whiteUsername() != null && gameData.whiteUsername().equals(username) && currentColor == ChessGame.TeamColor.WHITE) {
            return true;
        }
        return false;
    }

    private ServerMessage evaluateMove(int gameID, ChessGame game, ChessMove move) {
        try{
            game.makeMove(move);
            sqlGameDao.updateGameboard(gameID, game);
            return new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, game.getBoard());
        } catch (InvalidMoveException e) {
            return new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Invalid move");
        } catch (DataAccessException e) {
            System.out.println("Unable to connect to database");
        }
        return new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Unable to make move");
    }



    private AuthData verifyAuthToken(String authToken) {
        try {
            return sqlAuthDao.getAuth(authToken);
        } catch (DataAccessException e) {
            System.out.println("Unable to connect");
        }
        return null;
    }

    LoadGameMessage createLoadGameMessage(GameData gameData) {
        ChessGame game = gameData.game();
        return new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, game.getBoard());
    }
}
