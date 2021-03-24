package pt.ulisboa.tecnico.sec.secureserver.business.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pt.ulisboa.tecnico.sec.secureserver.business.domain.reports.Report;
import pt.ulisboa.tecnico.sec.secureserver.business.domain.reports.ReportCatalog;
import pt.ulisboa.tecnico.sec.secureserver.business.domain.users.User;
import pt.ulisboa.tecnico.sec.secureserver.business.domain.users.UserCatalog;
import pt.ulisboa.tecnico.sec.services.exceptions.InvalidReportException;

@Service
public class CreateReportHandler {
	
	private UserCatalog userCatalog;
	
	private ReportCatalog reportCatalog;
	
	@Autowired
	public CreateReportHandler(UserCatalog userCatalog, ReportCatalog reportCatalog) {
		this.userCatalog = userCatalog;
		this.reportCatalog = reportCatalog;
	}
	
	public void sumbitLocationReport(String userID, int epoch, String report) throws InvalidReportException {
		User currentUser = userCatalog.getUserById(userID);
		Report newReport = currentUser.createAndSaveReport(userID, epoch, report);
		reportCatalog.saveReport(newReport);
	}
	
}
