package pt.ulisboa.tecnico.sec.secureserver.business.domain.reports;

import org.hibernate.annotations.Type;
import pt.ulisboa.tecnico.sec.secureserver.business.domain.users.User;

import javax.persistence.*;

@Entity
@Table(name = "Proof")
public class ReportProof {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long identifier;

	@ManyToOne
	@JoinColumn
	private User user;

	@Column(nullable = false, name = "epoch")
	private int epoch;

	@ManyToOne
	@JoinColumn
	private Report report;

	@Column(nullable = false, name = "digital_signature")
	@Type(type = "text")
	private String digitalSignature;

	public ReportProof() {}

	public ReportProof(User user, int epoch, Report report, String digitalSignature) {
		this.user = user;
		this.epoch = epoch;
		this.report = report;
		this.digitalSignature = digitalSignature;
	}

	/**
	 * @return the userId
	 */
	public User getUser() {
		return user;
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
