package pt.ulisboa.tecnico.sec.secureclient.exceptions;

import pt.ulisboa.tecnico.sec.services.exceptions.ApplicationException;

public class CommandNotRegisteredException extends ApplicationException {
	
	public CommandNotRegisteredException(String message) {
		super(message);
	}

	public CommandNotRegisteredException(String message, Exception e) {
		super(message, e);
	}

}
