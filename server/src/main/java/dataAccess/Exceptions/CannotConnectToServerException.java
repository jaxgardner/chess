package dataAccess.Exceptions;

public class CannotConnectToServerException extends DataAccessException{

    public CannotConnectToServerException(String message) {
        super(message);
    }
}
