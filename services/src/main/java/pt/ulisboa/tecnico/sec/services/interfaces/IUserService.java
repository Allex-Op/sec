package pt.ulisboa.tecnico.sec.services.interfaces;

import pt.ulisboa.tecnico.sec.services.exceptions.ApplicationException;

public interface IUserService {
	
	public String obtainLocationReport(String userID, int epoch);
	
	public void submitLocationReport(String userID, int epoch, String report) throws ApplicationException;

}
