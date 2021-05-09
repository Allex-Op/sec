package pt.ulisboa.tecnico.sec.services.dto;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RequestLocationDTO that = (RequestLocationDTO) o;
        return x == that.x && y == that.y && epoch == that.epoch && userIDSender.equals(that.userIDSender) && userIDRequested.equals(that.userIDRequested);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, epoch, userIDSender, userIDRequested);
    }
}
