import dataAccess.Exceptions.DataAccessException;
import server.Server;

public class Main {
    public static void main(String[] args) throws DataAccessException {
        Server chessServer = new Server();

        chessServer.run(8080);
    }
}