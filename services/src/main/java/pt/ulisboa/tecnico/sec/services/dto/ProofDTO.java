package pt.ulisboa.tecnico.sec.services.dto;


import java.util.Objects;

/**
 * Object issued by witnesses to the prover, to acknowledge the location of the prover
 */
public class ProofDTO {

    private int epoch;
    private String userID; // Witness
    private RequestProofDTO requestProofDTO;
    private String digitalSignature;

    public int getEpoch() { return epoch; }

    public void setEpoch(int epoch) {
        this.epoch = epoch;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public RequestProofDTO getRequestProofDTO() {
        return requestProofDTO;
    }

    public void setRequestProofDTO(RequestProofDTO requestProofDTO) {
        this.requestProofDTO = requestProofDTO;
    }

    public String getDigitalSignature() {
        return digitalSignature;
    }

    public void setDigitalSignature(String digitalSignature) {
        this.digitalSignature = digitalSignature;
    }

    @Override
    public String toString() {
        return " * Proof of epoch " + epoch + " made by user " + userID + " with the Digital Signature " + digitalSignature + " of {" + ( requestProofDTO == null ? "NULL" : requestProofDTO.toString() ) + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProofDTO proofDTO = (ProofDTO) o;
        return epoch == proofDTO.epoch && userID.equals(proofDTO.userID) && requestProofDTO.equals(proofDTO.requestProofDTO) && digitalSignature.equals(proofDTO.digitalSignature);
    }

    @Override
    public int hashCode() {
        return Objects.hash(epoch, userID, requestProofDTO, digitalSignature);
    }

}
