package exception;

public class ServiceLogicException extends Exception{
    final private int statusCode;

    public ServiceLogicException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public int statusCode() {
        return statusCode;
    }
}

