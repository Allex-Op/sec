package pt.ulisboa.tecnico.sec.secureserver.business.domain.reports;

public class ReportProof {
	
	private String userId;
	
	private int epoch;
	
	private Report report;
	
	private String digitalSignature;
	
	public ReportProof(String userId, int epoch, Report report, String digitalSignature) {
		this.userId = userId;
		this.epoch = epoch;
		this.report = report;
		this.digitalSignature = digitalSignature;
	}

	/**
	 * @return the userId
	 */
	public String getUserId() {
		return userId;
	}

	/**
	 * @return the epoch
	 */
	public int getEpoch() {
		return epoch;
	}

	/**
	 * @return the report
	 */
	public Report getReport() {
		return report;
	}

	/**
	 * @return the digitalSignature
	 */
	public String getDigitalSignature() {
		return digitalSignature;
	}

}
