package pt.ulisboa.tecnico.sec.services.dto;

public class ProofDTO {

    private String nonce;
    private String userID; // Witness
    private RequestProofDTO requestProofDTO;
    private String digitalSignature;

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
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

}
