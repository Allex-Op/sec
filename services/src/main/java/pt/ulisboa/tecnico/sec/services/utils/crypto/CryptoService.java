package pt.ulisboa.tecnico.sec.services.utils.crypto;

import com.fasterxml.jackson.databind.ObjectMapper;
import pt.ulisboa.tecnico.sec.services.configs.CryptoConfiguration;
import pt.ulisboa.tecnico.sec.services.configs.PathConfiguration;
import pt.ulisboa.tecnico.sec.services.dto.ReportDTO;
import pt.ulisboa.tecnico.sec.services.dto.SecureDTO;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.Random;

public class CryptoService {

    /**
     *  Extracts the data field from a SecureDTO, decrypts it and converts it to a
     *  DTO Object.
     */
    public static Object extractEncryptedData(SecureDTO sec, Class<?> aClass) {
        try {
            // Obtain encrypted secret key in base64
            String randomString = sec.getRandomString();

            // Generate Secret Key
            SecretKey originalKey = generateSecretKey(randomString);

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
            byte[] randomBytes = generateRandomBytes();

            // Use random bytes to generate a symmetric AES Key
            SecretKey key = createSharedKeyFromString(randomBytes);

            // Convert dataDTO (e.g. ReportDTO) to a string
            ObjectMapper mapper = new ObjectMapper();
            String stringDTO = mapper.writeValueAsString(dataDTO);

            // Encrypt DataDTO with AES with the previously generated key
            String encryptedData = CryptoUtils.encrypt(key, stringDTO);

            // Encrypt the random bytes with the server public key, so he can decrypt it later
            KeyPair kp = RSAKeyGenerator.readKeyPairFromFile(PathConfiguration.SERVER_PUBLIC_KEY, PathConfiguration.SERVER_PRIVATE_KEY);
            byte[] encryptedRandomBytes = encrypt(randomBytes, kp.getPublic());
            String base64encryptedBytes = CryptoUtils.encodeBase64(encryptedRandomBytes);

            // Build the secureDTO
            return new SecureDTO(encryptedData, base64encryptedBytes,"bbb");
        } catch(Exception e) {
            System.out.println("Error creating secureDTO...");
        }

        return null;
    }

    /**
     *  Generate a shared key from random bytes, to encrypt the data field of the
     *  SecureDTO.
     */
    public static SecretKeySpec createSharedKeyFromString(byte[] randomBytes) {
        // Generating secretKey from randomString
        return new SecretKeySpec(randomBytes, 0, randomBytes.length, CryptoConfiguration.SYMMETRIC_ENCRYPTION_ALGO);
    }

    /**
     *  Used by the server to generate a shared key from a string.
     *
     *  First base64 decode the binary data, which is RSA encrypted with the server public key.
     *  With the decrypted bytes then, generate the shared key.
     */
    public static SecretKeySpec generateSecretKey(String randomString) throws NoSuchAlgorithmException, IOException, InvalidKeySpecException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchPaddingException {
        // Getting the encrypted random string from CustomProtocolResponse
        byte[] encryptedStringBytes = CryptoUtils.decodeBase64(randomString);

        // Extract private key from SERVER
        KeyPair kp = RSAKeyGenerator.readKeyPairFromFile(PathConfiguration.SERVER_PUBLIC_KEY, PathConfiguration.SERVER_PRIVATE_KEY);

        // Decrypt random string received
        byte[] decryptedStringBytes = decrypt(encryptedStringBytes, kp.getPrivate());

        return new SecretKeySpec(decryptedStringBytes, 0, decryptedStringBytes.length, CryptoConfiguration.SYMMETRIC_ENCRYPTION_ALGO);
    }

    /**
     *  Decrypt RSA
     */
    public static byte[] decrypt(byte[] data, PrivateKey privateKey) throws NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException {
        Cipher cipher = Cipher.getInstance(CryptoConfiguration.ASYMMETRIC_ENCRYPTION_ALGO);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(data);
    }

    /**
     *  Encrypt RSA
     */
    public static byte[] encrypt(byte[] data, PublicKey publicKey) throws NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException {
        Cipher cipher = Cipher.getInstance(CryptoConfiguration.ASYMMETRIC_ENCRYPTION_ALGO);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(data);
    }

    /**
     *  Generates random bytes
     */
    public static byte[] generateRandomBytes() {
        // Generating random string
        byte[] randomString = new byte[32];
        new Random().nextBytes(randomString);
        return randomString;
    }
}
