package pt.ulisboa.tecnico.sec.services.interfaces;

import pt.ulisboa.tecnico.sec.services.dto.ReportDTO;
import pt.ulisboa.tecnico.sec.services.exceptions.ApplicationException;

public interface IUserService {
	
	public ReportDTO obtainLocationReport(String userID, int epoch);
	
	public void submitLocationReport(String userID, ReportDTO reportDTO) throws ApplicationException;

}
