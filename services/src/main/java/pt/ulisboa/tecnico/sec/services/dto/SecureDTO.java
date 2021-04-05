package pt.ulisboa.tecnico.sec.services.dto;

public class SecureDTO {
    private String data;
    private String randomString;
    private String digitalSignature;
    private String iv;

    public SecureDTO() {}

    public SecureDTO(String data, String randomString, String digitalSignature, String iv) {
        this.data = data;
        this.randomString = randomString;
        this.digitalSignature = digitalSignature;
        this.iv = iv;
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

    public String getIv() {
        return iv;
    }

    public void setIv(String iv) {
        this.iv = iv;
    }

    @Override
    public String toString() {
        return "SecureDTO{" +
                "data='" + data + '\'' +
                ", randomString='" + randomString + '\'' +
                ", digitalSignature='" + digitalSignature + '\'' +
                ", IV='" + iv + '\'' +
                '}';
    }
}
