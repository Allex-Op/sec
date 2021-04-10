package pt.ulisboa.tecnico.sec.secureclient.exceptions;

import pt.ulisboa.tecnico.sec.services.exceptions.ApplicationException;

public class NotSufficientArgumentsException extends ApplicationException {
	
	public NotSufficientArgumentsException(String message) {
		super(message);
	}

	public NotSufficientArgumentsException(String message, Exception e) {
		super(message, e);
	}

	

}
