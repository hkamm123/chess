package service;

public class ServiceException extends Exception {
    public enum ServiceExceptionType {
        ALREADY_TAKEN,
        BAD_REQUEST,
        UNAUTHORIZED,
        SERVER_ERROR
    }

    private final ServiceExceptionType type;
    public ServiceException(ServiceExceptionType type) {
        super();
        this.type = type;
    }

    public ServiceExceptionType getType() {
        return type;
    }
}
