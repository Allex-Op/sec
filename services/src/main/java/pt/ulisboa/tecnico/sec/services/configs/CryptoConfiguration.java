package pt.ulisboa.tecnico.sec.services.configs;

public class CryptoConfiguration {

    // Key sizes
    public static final int SYMMETRIC_KEY_SIZE = 256;
    public static final int ASYMMETRIC_KEY_SIZE = 2048;

    // Algorithms
    public static final String ASYMMETRIC_ENCRYPTION_ALGO = "RSA";
    public static final String SYMMETRIC_ENCRYPTION_ALGO = "AES";
    public static final String CIPHER_ALGO = SYMMETRIC_ENCRYPTION_ALGO + "/ECB/PKCS5Padding";   //Replace by CBC in the future for super extra security, needs IV or parameter missing exception (add to SecureDTO object)
    public static final String SIGN_ALGO = "SHA256withRSA";

    private CryptoConfiguration(){}

}
