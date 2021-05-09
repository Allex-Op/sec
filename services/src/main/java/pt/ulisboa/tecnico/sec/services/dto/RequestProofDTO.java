package pt.ulisboa.tecnico.sec.services.dto;


import java.util.Objects;

/**
 * A request made by the prover sent to witnesses, to ask for location acknowledgement
 */
public class RequestProofDTO {

    private int x;
    private int y;
    private int epoch;
    private String userID; // Prover
    private String digitalSignature;
    private String nonce;

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getEpoch() {
        return epoch;
    }

    public void setEpoch(int epoch) {
        this.epoch = epoch;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getDigitalSignature() {
        return digitalSignature;
    }

    public void setDigitalSignature(String digitalSignature) {
        this.digitalSignature = digitalSignature;
    }

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    @Override
    public String toString() {
        return "Request of epoch " + epoch + " at location (" + x + "," + y + ") made by user " + userID + " with the Digital Signature " + digitalSignature;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RequestProofDTO that = (RequestProofDTO) o;
        return x == that.x && y == that.y && epoch == that.epoch && userID.equals(that.userID) && digitalSignature.equals(that.digitalSignature) && nonce.equals(that.nonce);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, epoch, userID, digitalSignature, nonce);
    }
}
