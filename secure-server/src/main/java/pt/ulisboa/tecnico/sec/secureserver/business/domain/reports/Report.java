package pt.ulisboa.tecnico.sec.secureserver.business.domain.reports;

public class Report {
	
	private String userId;
	
	private int epoch;
	
	private String location;
	
	public Report(String userId, int epoch, String location) {
		this.userId = userId;
		this.epoch = epoch;
		this.location = location;
	}

	/**
	 * @return the userId of the report's owner
	 */
	public String getUserId() {
		return userId;
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
	
	@Override
	public String toString() {
		return "Report of " + userId + " at " + epoch + " epoch. Location: " + location.toString();
	}

}
