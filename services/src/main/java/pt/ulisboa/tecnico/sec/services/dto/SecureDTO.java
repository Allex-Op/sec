package pt.ulisboa.tecnico.sec.services.dto;

public class SecureDTO {
    private String data;
    private String randomString;
    private String digitalSignature;

    public SecureDTO(String data, String randomString, String digitalSignature) {
        this.data = data;
        this.randomString = randomString;
        this.digitalSignature = digitalSignature;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getRandomString() {
        return randomString;
    }

    public void setRandomString(String randomString) {
        this.randomString = randomString;
    }

    public String getDigitalSignature() {
        return digitalSignature;
    }

    public void setDigitalSignature(String digitalSignature) {
        this.digitalSignature = digitalSignature;
    }
}
