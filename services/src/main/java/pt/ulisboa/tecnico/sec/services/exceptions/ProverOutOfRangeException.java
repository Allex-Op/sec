package pt.ulisboa.tecnico.sec.services.exceptions;

public class ProverOutOfRangeException extends ApplicationException {
    public ProverOutOfRangeException(String message) {
        super(message);
    }

    public ProverOutOfRangeException(String message, Exception e) {
        super(message, e);
    }
}
