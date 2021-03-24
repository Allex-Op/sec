package pt.ulisboa.tecnico.sec.secureserver.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pt.ulisboa.tecnico.sec.secureserver.business.handlers.CreateReportHandler;
import pt.ulisboa.tecnico.sec.secureserver.business.handlers.ViewReportHandler;
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
	public String obtainLocationReport(String userID, int epoch) {
		return this.viewReportHandler.obtainLocationReport(userID, epoch);
	}
	
	@Override
	public void submitLocationReport(String userID, int epoch, String report) throws ApplicationException {
		this.createReportHandler.sumbitLocationReport(userID, epoch, report);
	}

	@Override
	public List<String> obtainUsersAtLocation(String pos, int epoch) {
		return this.viewReportHandler.obtainUsersAtLocation(pos, epoch);
	}

}