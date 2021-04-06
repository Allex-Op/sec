package pt.ulisboa.tecnico.sec.secureserver.business.domain.users;

import java.util.ArrayList;
import java.util.List;
import pt.ulisboa.tecnico.sec.secureserver.business.domain.reports.Report;
import pt.ulisboa.tecnico.sec.services.exceptions.InvalidReportException;

import javax.persistence.*;

import static javax.persistence.CascadeType.ALL;

@Entity
@Table(name = "Client")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long identifier;

	@Column(unique = true, nullable = false, name = "user_id")
	private String userId;
	
	@Column(nullable = false, name = "is_special_user")
	private int isSpecialUser = 0; // 0 == not special : 1 == special

	@Column(nullable = false, name = "public_key")
	private String publicKey;

	@OneToMany(cascade = ALL, mappedBy = "user")
	private List<Report> reports = new ArrayList<>();
	
	public User(String userId) {
		this.userId = userId;
	}
	
	/**
	 * @return the userId
	 */
	public String getUserId() {
		return userId;
	}

	/**
	 * @return the reports
	 */
	public List<Report> getReports() {
		return reports;
	}
	
	public Report createAndSaveReport(String userId, int epoch, int x, int y, String digitalSignature) throws InvalidReportException {
		Report newReport = new Report(this, epoch, x, y, digitalSignature);
		
		this.reports.add(newReport);
		return newReport;
	}
	
	public Report getReportOfEpoch(int epoch) {
		for (Report report : this.reports)
			if (report.getEpoch() == epoch) return report;
		
		return null;
	}
	
	public void setAsSpecialUser() {
		this.isSpecialUser = 1;
	}
	
	public boolean isSpecialUser() {
		return this.isSpecialUser != 0;
	}
	
	@Override
	public String toString() {
		// building report list String
		StringBuilder sb = new StringBuilder();
		for (Report report : reports)
			sb.append(report.toString() + "\n");
		
		return "I am " + userId + " and i have the following Reports:\n" + sb.toString();
	}
	
}
