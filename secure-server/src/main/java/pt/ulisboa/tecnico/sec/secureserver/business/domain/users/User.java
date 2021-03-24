package pt.ulisboa.tecnico.sec.secureserver.business.domain.users;

import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.sec.secureserver.business.domain.reports.Report;
import pt.ulisboa.tecnico.sec.services.exceptions.InvalidReportException;

public class User {
	
	private String userId;
	
	private List<Report> reports = new ArrayList<>();
	
	public User(String userId) {
		this.userId = userId;
	}
	
	public Report createAndSaveReport(String userId, int epoch, String report) throws InvalidReportException {
		Report newReport = new Report(userId, epoch, report);
		
		if (!isValidReport(newReport))
			throw new InvalidReportException("Report is not valid. A report must not be empty and should be MY report.");
		
		this.reports.add(newReport);
		return newReport;
	}
	
	public Report getReportOfEpoch(int epoch) {
		for (Report report : this.reports)
			if (report.getEpoch() == epoch) return report;
		
		return null;
	}
	
	@Override
	public String toString() {
		// building report list String
		StringBuilder sb = new StringBuilder();
		for (Report report : reports)
			sb.append(report.toString() + "\n");
		
		return "I am " + userId + " and i have the following Reports:\n" + sb.toString();
	}
	
	// PRIVATES
	
	private boolean isValidReport(Report report) {
		return report != null && report.getUserId().equals(this.userId);
	}

}
