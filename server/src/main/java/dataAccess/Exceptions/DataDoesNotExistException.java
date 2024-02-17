package dataAccess.Exceptions;

public class DataDoesNotExistException extends DataAccessException{

    public DataDoesNotExistException(String message) {
        super(message);
    }
}
