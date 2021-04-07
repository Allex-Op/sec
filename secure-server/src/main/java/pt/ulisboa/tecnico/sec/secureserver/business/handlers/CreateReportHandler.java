package pt.ulisboa.tecnico.sec.secureserver.business.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pt.ulisboa.tecnico.sec.secureserver.ServerApplication;
import pt.ulisboa.tecnico.sec.secureserver.business.domain.reports.Report;
import pt.ulisboa.tecnico.sec.secureserver.business.domain.reports.ReportCatalog;
import pt.ulisboa.tecnico.sec.secureserver.business.domain.reports.ReportProof;
import pt.ulisboa.tecnico.sec.secureserver.business.domain.users.User;
import pt.ulisboa.tecnico.sec.secureserver.business.domain.users.UserCatalog;
import pt.ulisboa.tecnico.sec.services.configs.ByzantineConfigurations;
import pt.ulisboa.tecnico.sec.services.dto.ProofDTO;
import pt.ulisboa.tecnico.sec.services.dto.ReportDTO;
import pt.ulisboa.tecnico.sec.services.dto.RequestProofDTO;
import pt.ulisboa.tecnico.sec.services.exceptions.ApplicationException;
import pt.ulisboa.tecnico.sec.services.exceptions.InvalidReportException;

import java.util.ArrayList;
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
	
	public void submitLocationReport(String userID, ReportDTO reportDTO) throws ApplicationException {
		User currentUser = userCatalog.getUserById(userID);

		RequestProofDTO requestProofDTO = reportDTO.getRequestProofDTO();
		List<ProofDTO> proofDTOList = reportDTO.getProofsList();

		Report newReport = currentUser.createAndSaveReport(userID, requestProofDTO.getEpoch(), requestProofDTO.getX(), requestProofDTO.getY(), requestProofDTO.getDigitalSignature());

		List<ReportProof> reportProofList = createReportProofs(newReport, proofDTOList);
		newReport.setReportProofList(reportProofList);

		reportCatalog.saveReport(newReport);
	}

	/**
	 * 	Create a proof list
	 */
	private List<ReportProof> createReportProofs(Report report, List<ProofDTO> proofs) throws ApplicationException {
		List<ReportProof> reportProofList = new ArrayList<>();
		for (ProofDTO proof : proofs) {
			User user = userCatalog.getUserById(proof.getUserID());
			ReportProof reportProof = new ReportProof(user, proof.getEpoch(), report, proof.getDigitalSignature());
			reportProofList.add(reportProof);
		}
		return reportProofList;
	}


	private List verify (List<ProofDTO> proofDTOList){
		return null;
	}

	private void verifyReport(RequestProofDTO requestProofDTO, List<ProofDTO> proofDTOList) throws ApplicationException {

		if (requestProofDTO.getEpoch() > ServerApplication.epoch)
			throw new InvalidReportException("Desynchronized epoch.");

		if (proofDTOList.size() > ByzantineConfigurations.MINIMUM_BYZ_QUORUM)
			throw new InvalidReportException("Not enough Proofs to approve the Report.");

	}
}
