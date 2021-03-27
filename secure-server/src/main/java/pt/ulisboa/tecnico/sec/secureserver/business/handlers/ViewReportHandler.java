package pt.ulisboa.tecnico.sec.secureserver.business.handlers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pt.ulisboa.tecnico.sec.secureserver.business.domain.reports.Report;
import pt.ulisboa.tecnico.sec.secureserver.business.domain.reports.ReportCatalog;
import pt.ulisboa.tecnico.sec.secureserver.business.domain.users.User;
import pt.ulisboa.tecnico.sec.secureserver.business.domain.users.UserCatalog;

@Service
public class ViewReportHandler {
	
	private UserCatalog userCatalog;
	
	private ReportCatalog reportCatalog;
	
	@Autowired
	public ViewReportHandler(UserCatalog userCatalog, ReportCatalog reportCatalog) {
		this.userCatalog = userCatalog;
		this.reportCatalog = reportCatalog;
	}
	
	public String obtainLocationReport(String userID, int epoch) {
		User user = userCatalog.getUserById(userID);
		Report report = user.getReportOfEpoch(epoch);
		return report.toString(); //TODO create DTO
	}
	
	public List<String> obtainUsersAtLocation(String pos, int epoch) {
		List<Report> reportsFound = reportCatalog.getReportsOfLocationAt(pos, epoch);
		List<String> users = new ArrayList<>();
		
		for (Report report : reportsFound) {
			User user = userCatalog.getUserById(report.getUser().getUserId());
			users.add(user.toString());
		}
		
		return users; //TODO create DTO
	}

}
