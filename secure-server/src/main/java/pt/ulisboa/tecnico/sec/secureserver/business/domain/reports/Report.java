package pt.ulisboa.tecnico.sec.secureserver.business.domain.reports;

import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.sec.secureserver.business.domain.users.User;

public class Report {
	
	private User user;
	
	private int epoch;
	
	private String location;
	
	private List<ReportProof> reportProofList = new ArrayList<>();
	
	public Report(User user, int epoch, String location) {
		this.user = user;
		this.epoch = epoch;
		this.location = location;
	}

	/**
	 * @return the userId of the report's owner
	 */
	public User getUser() {
		return user;
	}

	/**
	 * @return the epoch of the report
	 */
	public int getEpoch() {
		return epoch;
	}

	/**
	 * @return the location of the report
	 */
	public String getLocation() {
		return location;
	}
	
	/**
	 * @return the reportProofList
	 */
	public List<ReportProof> getReportProofList() {
		return reportProofList;
	}
	
	@Override
	public String toString() {
		return "Report of " + user + " at " + epoch + " epoch. Location: " + location.toString();
	}

}
