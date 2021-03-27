package pt.ulisboa.tecnico.sec.secureserver.business.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pt.ulisboa.tecnico.sec.secureserver.business.domain.reports.Report;
import pt.ulisboa.tecnico.sec.secureserver.business.domain.reports.ReportCatalog;
import pt.ulisboa.tecnico.sec.secureserver.business.domain.reports.ReportProof;
import pt.ulisboa.tecnico.sec.secureserver.business.domain.users.User;
import pt.ulisboa.tecnico.sec.secureserver.business.domain.users.UserCatalog;
import pt.ulisboa.tecnico.sec.services.dto.ProofDTO;
import pt.ulisboa.tecnico.sec.services.dto.ReportDTO;
import pt.ulisboa.tecnico.sec.services.dto.RequestProofDTO;
import pt.ulisboa.tecnico.sec.services.exceptions.InvalidReportException;

import java.util.List;

@Service
public class CreateReportHandler {
	
	private UserCatalog userCatalog;
	
	private ReportCatalog reportCatalog;
	
	@Autowired
	public CreateReportHandler(UserCatalog userCatalog, ReportCatalog reportCatalog) {
		this.userCatalog = userCatalog;
		this.reportCatalog = reportCatalog;
	}
	
	public void submitLocationReport(String userID, ReportDTO reportDTO) throws InvalidReportException {
		User currentUser = userCatalog.getUserById(userID);

		RequestProofDTO requestProofDTO = reportDTO.getRequestProofDTO();
		List<ProofDTO> proofDTOList = reportDTO.getProofsList();

		Report newReport = currentUser.createAndSaveReport(userID, requestProofDTO.getEpoch(), requestProofDTO.getX(), requestProofDTO.getY(), requestProofDTO.getDigitalSignature());

		List<ReportProof> reportProofList = reportCatalog.creteReportProofs(proofDTOList, newReport);
		newReport.setReportProofList(reportProofList);

		reportCatalog.saveReport(newReport);
	}
	
}
