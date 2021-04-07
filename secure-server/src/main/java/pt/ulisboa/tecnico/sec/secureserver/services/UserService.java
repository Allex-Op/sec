package pt.ulisboa.tecnico.sec.secureserver.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pt.ulisboa.tecnico.sec.secureserver.business.handlers.CreateReportHandler;
import pt.ulisboa.tecnico.sec.secureserver.business.handlers.ViewReportHandler;
import pt.ulisboa.tecnico.sec.services.dto.ReportDTO;
import pt.ulisboa.tecnico.sec.services.dto.SpecialUserResponseDTO;
import pt.ulisboa.tecnico.sec.services.exceptions.ApplicationException;
import pt.ulisboa.tecnico.sec.services.interfaces.ISpecialUserService;

@Service
public class UserService implements ISpecialUserService {
	
	private CreateReportHandler createReportHandler;
	
	private ViewReportHandler viewReportHandler;
	
	@Autowired
	public UserService(CreateReportHandler createReportHandler, ViewReportHandler viewReportHandler) {
		this.createReportHandler = createReportHandler;
		this.viewReportHandler = viewReportHandler;
	}

	@Override
	public ReportDTO obtainLocationReport(String userID, int epoch) throws ApplicationException {
		return this.viewReportHandler.obtainLocationReport(userID, epoch);
	}
	
	@Override
	public void submitLocationReport(String userID, ReportDTO reportDTO) throws ApplicationException {
		this.createReportHandler.submitLocationReport(userID, reportDTO);
	}

	@Override
	public SpecialUserResponseDTO obtainUsersAtLocation(String userId, int x, int y, int epoch) throws ApplicationException {
		return this.viewReportHandler.obtainUsersAtLocation(userId, x, y, epoch);
	}

}
