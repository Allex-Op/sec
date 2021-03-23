package pt.ulisboa.tecnico.sec.services.interfaces;

public interface IUserService {
	
	public void submitLocationReport(String userID, int epoch, String report);
	
	public String obtainLocationReport(String userID, int epoch);

}
