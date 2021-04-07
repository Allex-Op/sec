package pt.ulisboa.tecnico.sec.secureserver.business.domain.reports;

import java.util.List;

import org.springframework.stereotype.Repository;
import pt.ulisboa.tecnico.sec.services.exceptions.ApplicationException;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

@Repository
public class ReportCatalog {

	@PersistenceContext
	private EntityManager em;

	@Transactional(Transactional.TxType.REQUIRES_NEW)
	public void saveReport(Report report) {
		em.persist(report);
	}

	/**
	 *	Special user asks for all reports at certain epoch and location
	 */
	public List<Report> getReportsOfLocationAt(int x, int y, int epoch) throws ApplicationException {
		try {
			TypedQuery<Report> query = em.createNamedQuery(Report.FIND_REPORT_BY_EPOCH_AND_LOCATION, Report.class);
			query.setParameter(Report.FIND_REPORT_BY_EPOCH_AND_LOCATION_EPOCH, epoch);
			query.setParameter(Report.FIND_REPORT_BY_EPOCH_AND_LOCATION_LOCATION_X, x);
			query.setParameter(Report.FIND_REPORT_BY_EPOCH_AND_LOCATION_LOCATION_Y, y);

			return query.getResultList();
		} catch(Exception e) {
			throw new ApplicationException("Error at getReportsOfLocationAt class ReportCatalog.");
		}
	}

	/**
	 * Retrieve report from a user at a certain epoch
	 */
	public Report getReportOfUserIdAtEpoch(String userId, int epoch) throws ApplicationException {
		try {


			TypedQuery<Report> query = em.createNamedQuery(Report.FIND_REPORT_BY_USER_ID_AT_EPOCH, Report.class);
			query.setParameter(Report.FIND_REPORT_BY_USER_ID_AT_EPOCH_EPOCH, epoch);
			query.setParameter(Report.FIND_REPORT_BY_USER_ID_AT_EPOCH_USER_ID, userId);

			return query.getSingleResult();
		} catch(NoResultException e) {
			System.out.println("No report found.");
			return null;
		} catch(Exception e) {
			e.printStackTrace();
			throw new ApplicationException("Error at getReportOfUserIdAtEpoch at class ReportCatalog.");
		}
	}
}
