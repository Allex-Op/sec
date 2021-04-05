package pt.ulisboa.tecnico.sec.services.utils.crypto;

import com.fasterxml.jackson.databind.ObjectMapper;
import pt.ulisboa.tecnico.sec.services.configs.CryptoConfiguration;
import pt.ulisboa.tecnico.sec.services.configs.PathConfiguration;
import pt.ulisboa.tecnico.sec.services.dto.SecureDTO;

import javax.crypto.*;
import java.io.IOException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;

public class CryptoService {
	
	private CryptoService() {}


    /**
     *  Extracts secret key from the SecureDTO object, only called by the server
     *  as the client already possesses the key.
     */
	public static SecretKey getSecretKeyFromDTO(SecureDTO sec) {
	    try {
            // Obtain encrypted secret key in base64
            String randomString = sec.getRandomString();

            // Getting the encrypted random string from Custom Protocol Response
            byte[] encryptedStringBytes = CryptoUtils.decodeBase64(randomString);
            KeyPair kp = CryptoUtils.readKeyPairFromFile(PathConfiguration.SERVER_PUBLIC_KEY, PathConfiguration.SERVER_PRIVATE_KEY);
            byte[] decryptedStringBytes = CryptoUtils.decrypt(encryptedStringBytes, kp.getPrivate());

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
            KeyPair kp = CryptoUtils.readKeyPairFromFile(PathConfiguration.SERVER_PUBLIC_KEY, PathConfiguration.SERVER_PRIVATE_KEY);
            byte[] encryptedRandomBytes = CryptoUtils.encrypt(randomBytes, kp.getPublic());
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


    /**
     *  Creates a SecureDTO
     */
    public static SecureDTO createSecureDTO(Object dataDTO, SecretKey key, String randomBytesEncoded) {
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

            // Build the secureDTO
            SecureDTO sec = new SecureDTO(encryptedData, randomBytesEncoded,"bbb", ivString);
            System.out.println(sec);
            return sec;
        } catch(Exception e) {
            System.out.println("Error creating secureDTO...");
        }

        return null;
    }

}
