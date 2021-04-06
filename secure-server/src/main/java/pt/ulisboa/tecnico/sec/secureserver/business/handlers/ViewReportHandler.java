package pt.ulisboa.tecnico.sec.secureserver.business.handlers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pt.ulisboa.tecnico.sec.secureserver.business.domain.reports.Report;
import pt.ulisboa.tecnico.sec.secureserver.business.domain.reports.ReportCatalog;
import pt.ulisboa.tecnico.sec.secureserver.business.domain.users.User;
import pt.ulisboa.tecnico.sec.secureserver.business.domain.users.UserCatalog;
import pt.ulisboa.tecnico.sec.secureserver.utils.DTOConverter;
import pt.ulisboa.tecnico.sec.services.dto.ReportDTO;
import pt.ulisboa.tecnico.sec.services.dto.SpecialUserResponseDTO;
import pt.ulisboa.tecnico.sec.services.exceptions.NoRequiredPrivilegesException;

@Service
public class ViewReportHandler {
	
	private UserCatalog userCatalog;
	
	private ReportCatalog reportCatalog;
	
	@Autowired
	public ViewReportHandler(UserCatalog userCatalog, ReportCatalog reportCatalog) {
		this.userCatalog = userCatalog;
		this.reportCatalog = reportCatalog;
	}
	
	public ReportDTO obtainLocationReport(String userID, int epoch) {
		User user = userCatalog.getUserById(userID);
		Report report = user.getReportOfEpoch(epoch);
		return DTOConverter.makeReportDTO(report);
	}
	
	public SpecialUserResponseDTO obtainUsersAtLocation(String userId, String pos, int epoch) throws NoRequiredPrivilegesException {
		User user = userCatalog.getUserById(userId);
		if (!user.isSpecialUser())
			throw new NoRequiredPrivilegesException("The user cannot do this task because it is not a special user.");
		
		List<Report> reportsFound = reportCatalog.getReportsOfLocationAt(pos, epoch);
		List<User> users = new ArrayList<>();
		
		for (Report report : reportsFound) {
			User userAtLocation = userCatalog.getUserById(report.getUser().getUserId());
			users.add(userAtLocation);
		}
		
		return DTOConverter.makeSpecialUserResponseDTO(users);
	}

}
