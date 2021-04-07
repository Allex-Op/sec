package pt.ulisboa.tecnico.sec.secureserver.business.domain.reports;

import java.util.List;

import org.springframework.stereotype.Repository;
import pt.ulisboa.tecnico.sec.services.exceptions.ApplicationException;

import javax.persistence.EntityManager;
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
}
