package pt.ulisboa.tecnico.sec.services.exceptions;

/**
 * Exception thrown when an repeated nonce is detected
 */
public class RepeatedNonceException extends ApplicationException {

    public RepeatedNonceException(String message) {
        super(message);
    }
}
