package pt.ulisboa.tecnico.sec.services.dto;

/**
 * DTO used by the server endpoints POST & GET /locations/ as nothing can be transmitted unprotecte
 */
public class RequestLocationDTO {
    private int x;  // Only important for special user endpoint
    private int y;  // Only important for special user endpoint
    private int epoch;
    private String userID; // Not important for special user endpoint

    public int getEpoch() {
        return epoch;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public String getUserID() {
        return userID;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setEpoch(int epoch) {
        this.epoch = epoch;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setY(int y) {
        this.y = y;
    }
}
