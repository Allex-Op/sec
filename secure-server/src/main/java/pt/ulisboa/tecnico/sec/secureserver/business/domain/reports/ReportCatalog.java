package pt.ulisboa.tecnico.sec.secureserver.business.domain.reports;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

@Repository
public class ReportCatalog {
	
	private Map<Integer, List<Report>> reports = new HashMap<>();
	
	public void saveReport(Report report) {
		int epoch = report.getEpoch();
		
		List<Report> epochReports = this.reports.get(epoch);
		if (epochReports == null)
			epochReports = new ArrayList<>();
		
		epochReports.add(report);
		this.reports.put(report.getEpoch(), epochReports);
	}

	public List<Report> getReportsOfLocationAt(String pos, int epoch) {
		List<Report> epochReports = reports.get(epoch);
		List<Report> result = new ArrayList<>();
		
		for (Report report : epochReports)
			if (report.getLocation().equals(pos))
				result.add(report);
		
		return result;
	}

}
