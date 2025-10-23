package service;

public class ServiceException extends Exception {
    private int statusCode;

    public ServiceException(String message, int statuscode) {
        super(message);
        this.statusCode = statuscode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
