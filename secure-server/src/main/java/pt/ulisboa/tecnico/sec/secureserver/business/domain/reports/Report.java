package pt.ulisboa.tecnico.sec.secureserver.business.domain.reports;

import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.sec.secureserver.business.domain.users.User;

public class Report {
	
	private User user;
	
	private int epoch;

	// location
	private int x, y;
	
	private List<ReportProof> reportProofList = new ArrayList<>();

	private String digitalSignature;

	public Report(User user, int epoch, int x, int y, String digitalSignature) {
		this.user = user;
		this.epoch = epoch;
		this.x = x;
		this.y = y;
		this.reportProofList = reportProofList;
		this.digitalSignature = digitalSignature;

	}

	/**
	 * @return the x of the location
	 */
	public int getX() {
		return x;
	}

	/**
	 * @return the y of the location
	 */
	public int getY() {
		return y;
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
	 * @return the reportProofList
	 */
	public List<ReportProof> getReportProofList() {
		return reportProofList;
	}

	/**
	 * @return the digitalSignature
	 */
	public String getDigitalSignature() {
		return digitalSignature;
	}

	public void setReportProofList(List<ReportProof> reportProofList) {
		this.reportProofList = reportProofList;
	}

	@Override
	public String toString() {
		return "Report of " + user + " at " + epoch + " epoch. Location: " + x + "," + y;
	}

}
