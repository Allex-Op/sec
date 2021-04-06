package pt.ulisboa.tecnico.sec.services.utils.crypto;

import com.fasterxml.jackson.databind.ObjectMapper;
import pt.ulisboa.tecnico.sec.services.configs.CryptoConfiguration;
import pt.ulisboa.tecnico.sec.services.dto.SecureDTO;

import javax.crypto.*;
import java.security.*;

public class CryptoService {
	
	private CryptoService() {}


    /**
     *  Extracts secret key from the SecureDTO object, only called by the server
     *  as the client already possesses the key.
     */
	// to be used by the server cuz it uses Server private key
	public static SecretKey getSecretKeyFromDTO(SecureDTO sec) {
	    try {
            // Obtain encrypted secret key in base64
            String randomString = sec.getRandomString();

            // Getting the encrypted random string from Custom Protocol Response
            byte[] encryptedStringBytes = CryptoUtils.decodeBase64(randomString);
            PrivateKey kp = CryptoUtils.getServerPrivateKey();
            byte[] decryptedStringBytes = CryptoUtils.decrypt(encryptedStringBytes, kp);

            // Generate Secret Key
            return CryptoUtils.createSharedKeyFromString(decryptedStringBytes);
        } catch(Exception e) {
            System.out.println("Error extracting secret key");
        }

	    return null;
    }

    /**
     *  Function only called by the client to generate a secret key from random bytes
     */
    public static SecretKey generateSecretKey(byte[] randomBytes) {
	    try {
            // Use random bytes to generate a symmetric AES Key
            return CryptoUtils.createSharedKeyFromString(randomBytes);
        } catch(Exception e) {
            System.out.println("Error generating secret key");
        }

	    return null;
    }

    /**
     *  Returns the randomBytes (used to create the shared key) encrypted and encoded in base64,
     *  only used by the client.
     */
    public static String encryptRandomBytes(byte[] randomBytes) {
        try {
            // Encrypt the random bytes with the server public key, so he can decrypt it later
            PublicKey pk = CryptoUtils.getServerPublicKey();
            byte[] encryptedRandomBytes = CryptoUtils.encrypt(randomBytes, pk);
            return CryptoUtils.encodeBase64(encryptedRandomBytes);
        } catch(Exception e) {
            System.out.println("Error encrypting random bytes with server public key");
        }
        return null;
    }

    /**
     *  Extracts the data field from a SecureDTO, decrypts it and converts it to a
     *  DTO Object.
     */
    public static Object extractEncryptedData(SecureDTO sec, Class<?> aClass, SecretKey originalKey) {
        try {
            // Use the original key to decrypt the data field
            String dataEncrypted = sec.getData();

            // Get IV
            String IVString = sec.getIv();
            byte[] IV = CryptoUtils.decodeBase64(IVString);

            // Get the original JSON Object as String
            String data = CryptoUtils.decrypt(originalKey, dataEncrypted, IV);
            System.out.println("Encrypted object was:" + data);

            // Convert string json to DTO
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(data, aClass);

        } catch(Exception e) {
            System.out.println("Error caught in the extractEncryptedData function...");
        }

        return null;
    }
    
    // to be used by the server cuz it uses Server private key
    public static Object extractEncryptedData(SecureDTO sec, Class<?> aClass) {
    	SecretKey originalKey = getSecretKeyFromDTO(sec);
    	return extractEncryptedData(sec, aClass, originalKey);
    }


    /**
     *  Creates a SecureDTO
     */
    public static SecureDTO createSecureDTO(Object dataDTO, SecretKey key, String randomBytesEncoded, PrivateKey signKey) {
        try {
            // Convert dataDTO (e.g. ReportDTO) to a string
            ObjectMapper mapper = new ObjectMapper();
            String stringDTO = mapper.writeValueAsString(dataDTO);

            // Generate IV
            byte[] ivBytes = new byte[CryptoConfiguration.IV_SIZE_BYTES];
            new SecureRandom().nextBytes(ivBytes);
            String ivString = CryptoUtils.encodeBase64(ivBytes);

            // Encrypt DataDTO with AES with the previously generated key
            String encryptedData = CryptoUtils.encrypt(key, stringDTO, ivBytes);

            // Build digitalSignature
            String digitalSignature = CryptoUtils.sign(signKey, encryptedData+randomBytesEncoded+ivString);

            // Build the secureDTO
            SecureDTO sec = new SecureDTO(encryptedData, randomBytesEncoded, digitalSignature, ivString);
            System.out.println(sec);
            return sec;
        } catch(Exception e) {
            System.out.println("Error creating secureDTO...");
        }

        return null;
    }

    /**
     * Check the digital signature of the secureDTO received
     * from the server.
     *
     * TODO: Currently it assumes there is only one server, and retrieves
     * that key, in case there are multiple servers with different keys
     * then this has to be changed.
     */
    public static boolean checkSecureDTODigitalSignature(SecureDTO sec, PublicKey pk) {
        try {
            return CryptoUtils.confirmSignature(
                    pk,
                    sec.getData() + sec.getRandomString() + sec.getIv(),
                    sec.getDigitalSignature());
        } catch(Exception e) {
            System.out.println("Digital signature check failed");
            return false;
        }
    }
    
    /**
     * Generates a new SecureDTO from user with userId specified that encapsulates 
     * a LocationReportDTO or a ReportDTO
     */
    public static <T> SecureDTO generateNewSecureDTO(T unsecureDTO, String userId, byte[] randomBytes) {
        SecretKey key = generateSecretKey(randomBytes);
        return createSecureDTO(unsecureDTO, key, encryptRandomBytes(randomBytes), CryptoUtils.getClientPrivateKey(userId));
    }
    
    /**
     * Generates a response SecureDTO of a client request that encapsulates a ReportDTO
     */
    public static <T> SecureDTO generateResponseSecureDTO(SecureDTO receivedSecureDTO, T unsecureResponseDTO) {
    	SecretKey key = getSecretKeyFromDTO(receivedSecureDTO);
    	return createSecureDTO(unsecureResponseDTO, key, "", CryptoUtils.getServerPrivateKey());
    }
}
