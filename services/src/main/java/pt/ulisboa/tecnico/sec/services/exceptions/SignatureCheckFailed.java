package pt.ulisboa.tecnico.sec.services.exceptions;

public class SignatureCheckFailed extends ApplicationException {
	
	/**
	 * The serial version id (generated automatically by Eclipse)
	 */
	private static final long serialVersionUID = 6712789933476593181L;

	public SignatureCheckFailed(String message) {
        super(message);
    }
	
	public SignatureCheckFailed(String message, Exception e) {
		super(message, e);
	}
    
}
