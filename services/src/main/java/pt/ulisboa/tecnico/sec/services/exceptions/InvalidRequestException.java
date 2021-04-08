package pt.ulisboa.tecnico.sec.services.exceptions;

public class InvalidRequestException extends ApplicationException {
    public InvalidRequestException(String message) {
        super(message);
    }
}
