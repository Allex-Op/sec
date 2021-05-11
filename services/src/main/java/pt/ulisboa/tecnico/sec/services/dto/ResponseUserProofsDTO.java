package pt.ulisboa.tecnico.sec.services.dto;

import java.util.List;

public class ResponseUserProofsDTO {
	
	private List<ProofDTO> proofs;

	public ResponseUserProofsDTO() {}

	public ResponseUserProofsDTO(List<ProofDTO> proofs) {
		this.proofs = proofs;
	}

	/**
	 * @return the proofs
	 */
	public List<ProofDTO> getProofs() {
		return proofs;
	}

}
