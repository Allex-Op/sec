package pt.ulisboa.tecnico.sec.services.utils.crypto;

import com.fasterxml.jackson.databind.ObjectMapper;
import pt.ulisboa.tecnico.sec.services.configs.PathConfiguration;
import pt.ulisboa.tecnico.sec.services.dto.SecureDTO;

import javax.crypto.*;
import java.security.*;

public class CryptoService {
	
	private CryptoService() {}

    /**
     *  Extracts the data field from a SecureDTO, decrypts it and converts it to a
     *  DTO Object.
     */
    public static Object extractEncryptedData(SecureDTO sec, Class<?> aClass) {
        try {
            // Obtain encrypted secret key in base64
            String randomString = sec.getRandomString();
            
            // Getting the encrypted random string from Custom Protocol Response
            byte[] encryptedStringBytes = CryptoUtils.decodeBase64(randomString);
            KeyPair kp = CryptoUtils.readKeyPairFromFile(PathConfiguration.SERVER_PUBLIC_KEY, PathConfiguration.SERVER_PRIVATE_KEY);
            byte[] decryptedStringBytes = CryptoUtils.decrypt(encryptedStringBytes, kp.getPrivate());
            
            // Generate Secret Key
            SecretKey originalKey = CryptoUtils.createSharedKeyFromString(decryptedStringBytes);

            // Use the original key to decrypt the data field
            String dataEncrypted = sec.getData();

            // Get the original JSON Object as String
            String data = CryptoUtils.decrypt(originalKey, dataEncrypted);  // Error

            // Convert string json to DTO
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(data, aClass);

        } catch(Exception e) {
            System.out.println("Error caught in the extractEncryptedData function...");
        }

        return null;
    }

    /**
     *  Creates a SecureDTO, to send to the server or client.
     */
    public static SecureDTO createSecureDTO(Object dataDTO) {
        try {
            // Generate random string
            byte[] randomBytes = CryptoUtils.generateRandom32Bytes();

            // Use random bytes to generate a symmetric AES Key
            SecretKey key = CryptoUtils.createSharedKeyFromString(randomBytes);

            // Convert dataDTO (e.g. ReportDTO) to a string
            ObjectMapper mapper = new ObjectMapper();
            String stringDTO = mapper.writeValueAsString(dataDTO);

            // Encrypt DataDTO with AES with the previously generated key
            String encryptedData = CryptoUtils.encrypt(key, stringDTO);

            // Encrypt the random bytes with the server public key, so he can decrypt it later
            KeyPair kp = CryptoUtils.readKeyPairFromFile(PathConfiguration.SERVER_PUBLIC_KEY, PathConfiguration.SERVER_PRIVATE_KEY);
            byte[] encryptedRandomBytes = CryptoUtils.encrypt(randomBytes, kp.getPublic());
            String base64encryptedBytes = CryptoUtils.encodeBase64(encryptedRandomBytes);

            // Build the secureDTO
            return new SecureDTO(encryptedData, base64encryptedBytes,"bbb");
        } catch(Exception e) {
            System.out.println("Error creating secureDTO...");
        }

        return null;
    }

}
