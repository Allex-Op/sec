package pt.ulisboa.tecnico.sec.services.dto;

/**
 * Message sent by clients to the servers asking for a location report,
 * doesn't require digitalSignature as this message is encapsulated in a SecureDTO.
 */
public class RequestLocationDTO {
    private int x;  // Only important for special user endpoint
    private int y;  // Only important for special user endpoint
    private int epoch;
    private String userIDSender;    // The userId of the person sending
    private String userIDRequested; // The userId of the report requested


    public int getEpoch() {
        return epoch;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public String getUserIDSender() {
        return userIDSender;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setEpoch(int epoch) {
        this.epoch = epoch;
    }

    public void setUserIDSender(String userIDSender) {
        this.userIDSender = userIDSender;
    }

    public void setY(int y) {
        this.y = y;
    }

    public String getUserIDRequested() {
        return userIDRequested;
    }

    public void setUserIDRequested(String userIDRequested) {
        this.userIDRequested = userIDRequested;
    }
}
