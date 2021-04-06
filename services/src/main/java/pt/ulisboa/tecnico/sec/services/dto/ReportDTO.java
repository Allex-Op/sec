package pt.ulisboa.tecnico.sec.services.dto;

import java.util.List;

/**
 * Server answer to client that asked for a location report,
 * doesn't require digitalSignature as this message is encapsulated in a SecureDTO.
 */
public class ReportDTO {

    private RequestProofDTO requestProofDTO;
    private List<ProofDTO> proofsList;

    public RequestProofDTO getRequestProofDTO() {
        return requestProofDTO;
    }

    public void setRequestProofDTO(RequestProofDTO requestProofDTO) {
        this.requestProofDTO = requestProofDTO;
    }

    public List<ProofDTO> getProofsList() {
        return proofsList;
    }

    public void setProofsList(List<ProofDTO> proofsList) {
        this.proofsList = proofsList;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (ProofDTO proofDTO : proofsList)
            sb.append(proofDTO.toString() + "\n");

        return  "Report:\n" + requestProofDTO.toString() + "\n" + sb.toString();
    }
}
