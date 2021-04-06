package pt.ulisboa.tecnico.sec.services.exceptions;

public class SignatureCheckFailed extends ApplicationException {
    public SignatureCheckFailed(String message) {
        super(message);
    }
}
