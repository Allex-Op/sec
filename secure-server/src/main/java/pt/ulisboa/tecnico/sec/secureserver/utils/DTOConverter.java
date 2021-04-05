package pt.ulisboa.tecnico.sec.secureserver.utils;

import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.sec.secureserver.business.domain.reports.Report;
import pt.ulisboa.tecnico.sec.secureserver.business.domain.reports.ReportProof;
import pt.ulisboa.tecnico.sec.services.dto.DTOFactory;
import pt.ulisboa.tecnico.sec.services.dto.ProofDTO;
import pt.ulisboa.tecnico.sec.services.dto.ReportDTO;
import pt.ulisboa.tecnico.sec.services.dto.RequestProofDTO;

public class DTOConverter {
	
	private DTOConverter() {}
	
	public static ReportDTO makeReportDTO(Report report) {
		// generate Request Proof DTO
		RequestProofDTO requestProofDTO = DTOFactory.makeRequestProofDTO(report.getX(), report.getY(), report.getEpoch(), report.getUser().getUserId(), report.getDigitalSignature());
		
		// generate the Proof List DTO
		List<ProofDTO> proofs = new ArrayList<>();
		for (ReportProof proof : report.getReportProofList()) {
			ProofDTO proofDTO = DTOFactory.makeProofDTO(proof.getEpoch(), proof.getUser().getUserId(), requestProofDTO, proof.getDigitalSignature());
			proofs.add(proofDTO);
		}
		
		return DTOFactory.makeReportDTO(requestProofDTO, proofs);
	}

}
