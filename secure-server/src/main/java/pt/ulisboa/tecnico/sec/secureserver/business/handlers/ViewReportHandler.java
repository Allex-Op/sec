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
import pt.ulisboa.tecnico.sec.services.exceptions.ApplicationException;
import pt.ulisboa.tecnico.sec.services.exceptions.InvalidRequestException;
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
	
	public ReportDTO obtainLocationReport(String userIdSender, String userIdRequested, int epoch) throws ApplicationException {
		User userSender = userCatalog.getUserById(userIdSender);
		User userRequest = userCatalog.getUserById(userIdRequested);

		if(userSender == null || userRequest == null)
			throw new InvalidRequestException("Request malformed");

		if(!userSender.isSpecialUser() && !userIdSender.equals(userIdRequested))
			throw new NoRequiredPrivilegesException("The sender id can not request the information of the requested id.");

		Report report = reportCatalog.getReportOfUserIdAtEpoch(userRequest.getUserId(), epoch);
		if(report == null)
			throw new InvalidRequestException("No report found.");

		return DTOConverter.makeReportDTO(report);
	}

	/**
	 *	Used by the special user to request all reports
	 */
	public SpecialUserResponseDTO obtainUsersAtLocation(String userId, int x, int y, int epoch) throws ApplicationException {
		User user = userCatalog.getUserById(userId);
		if (!user.isSpecialUser())
			throw new NoRequiredPrivilegesException("The user cannot do this task because it is not a special user.");
		
		List<Report> reportsFound = reportCatalog.getReportsOfLocationAt(x, y, epoch);
		List<User> users = new ArrayList<>();
		
		for (Report report : reportsFound) {
			User userAtLocation = userCatalog.getUserById(report.getUser().getUserId());
			users.add(userAtLocation);
		}
		
		return DTOConverter.makeSpecialUserResponseDTO(users);
	}

}
