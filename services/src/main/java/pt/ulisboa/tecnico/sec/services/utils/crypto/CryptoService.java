package pt.ulisboa.tecnico.sec.services.utils.crypto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.tools.javac.code.Types;
import pt.ulisboa.tecnico.sec.services.configs.CryptoConfiguration;
import pt.ulisboa.tecnico.sec.services.dto.ProofDTO;
import pt.ulisboa.tecnico.sec.services.dto.RequestProofDTO;
import pt.ulisboa.tecnico.sec.services.dto.SecureDTO;
import pt.ulisboa.tecnico.sec.services.exceptions.ApplicationException;
import pt.ulisboa.tecnico.sec.services.exceptions.SignatureCheckFailedException;

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
            Object converted = mapper.readValue(data, aClass);
            return converted;
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
    public static SecureDTO createSecureDTO(Object dataDTO, SecretKey key, String randomString, PrivateKey signKey) {
        try {
            // Convert dataDTO (e.g. ReportDTO) to a string
            ObjectMapper mapper = new ObjectMapper();
            String stringDTO = mapper.writeValueAsString(dataDTO);

            // Generate IV
            byte[] ivBytes = new byte[CryptoConfiguration.IV_SIZE_BYTES];
            new SecureRandom().nextBytes(ivBytes);
            String ivString = CryptoUtils.encodeBase64(ivBytes);

            // Encrypt DataDTO with AES with the previously generated key
            String data = CryptoUtils.encrypt(key, stringDTO, ivBytes);

            // Build the secureDTO
            SecureDTO sec = new SecureDTO(data, randomString, ivString, CryptoUtils.generateNonce());

            // Sign the secureDTO
            signSecureDTO(sec, signKey);

            System.out.println(sec);
            return sec;
        } catch(Exception e) {
            System.out.println("Error creating secureDTO...");
        }

        return null;
    }



    /**
     *  Verifies the digital signature of request report and proofs
     */
    public static boolean checkDigitalSignature(String message, String digitalSignature, PublicKey pk) {
        try {
            return CryptoUtils.confirmSignature(
                    pk,
                    message,
                    digitalSignature
            );
        } catch(Exception e) {
            System.out.println("Digital signature of report or proof failed.");
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

    /**
     * Builds the request proof message to be digitally signed.
     */
    public static String buildRequestProofMessage(RequestProofDTO reqProof) throws SignatureCheckFailedException {
        if(reqProof == null)
            throw new SignatureCheckFailedException("Can't validate proof signature, as the proof as no request associated with it.");
        return reqProof.getX() + reqProof.getY() + reqProof.getEpoch() + reqProof.getUserID();
    }

    /**
     * Builds the proof message to be digitally signed.
     */
    public static String buildProofMessage(ProofDTO proof) throws ApplicationException  {
        return proof.getEpoch() +
                proof.getUserID() +
                buildRequestProofMessage(proof.getRequestProofDTO()) +
                proof.getRequestProofDTO().getDigitalSignature();
    }

    /**
     * Sign Request DTO
     */
    public static void signRequestProofDTO(RequestProofDTO req) {
        try {
            req.setDigitalSignature(
                    CryptoUtils.sign(
                            CryptoUtils.getClientPrivateKey(req.getUserID()), buildRequestProofMessage(req)
                    )
            );
        } catch(Exception e) {
            System.out.println("Unable to sign the request proof, sending without signature even thought " +
                    "the failure is imminent and inevitable.");
        }
    }

    /**
     * Sign proof DTO
     */
    public static void signProofDTO(ProofDTO proof) {
        try {
            proof.setDigitalSignature(
                    CryptoUtils.sign(
                            CryptoUtils.getClientPrivateKey(proof.getUserID()), buildProofMessage(proof)
                    )
            );
        } catch(Exception e) {
            System.out.println("Unable to sign the proof, sending without signature even thought " +
                    "the failure is imminent and inevitable.");
        }
    }


    /************************************* Secure DTO Digital Signature Aux Funcs *************************************/

    /**
     * Build secureDTO message signature
     */
    public static String buildSecureDTOMessage(SecureDTO sec) {
        return sec.getData() + sec.getRandomString() + sec.getIv() + sec.getNonce();
    }


    /**
     * Sign secure dto
     */
    public static void signSecureDTO(SecureDTO sec, PrivateKey signKey) {
        try {
            sec.setDigitalSignature(
                    CryptoUtils.sign(
                            signKey, buildSecureDTOMessage(sec)
                    )
            );
        } catch(Exception e) {
            System.out.println("Unable to sign the secure DTO, sending without signature even thought " +
                    "the failure is imminent and inevitable.");
        }
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
        return checkDigitalSignature(
                buildSecureDTOMessage(sec),
                sec.getDigitalSignature(),
                pk
        );
    }
}
