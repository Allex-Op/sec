package pt.ulisboa.tecnico.sec.services.utils.crypto;

import pt.ulisboa.tecnico.sec.services.configs.CryptoConfiguration;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.Random;
import java.util.UUID;

public class CryptoUtils {
	
	private CryptoUtils() {}

    // Nonce and Randoms

    public static String generateNonce() {
        return UUID.randomUUID().toString();
    }

    public static SecureRandom generateSecureRandom() {
        return new SecureRandom();
    }
    
    public static byte[] generateRandom32Bytes() {
        byte[] randomBytes = new byte[32];
        new Random().nextBytes(randomBytes);
        return randomBytes;
    }

    // Encryption - Symmetric

    public static String encrypt(SecretKey secretKey, String dataToEncrypt) throws NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

        Cipher cipher = Cipher.getInstance(CryptoConfiguration.CIPHER_ALGO);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] cipherBytes = cipher.doFinal(dataToEncrypt.getBytes());
        return encodeBase64(cipherBytes);

    }

    public static String decrypt(SecretKey secretKey, String encryptedData) throws NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException  {

        Cipher cipher = Cipher.getInstance(CryptoConfiguration.CIPHER_ALGO);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] cipherBytes = cipher.doFinal(decodeBase64(encryptedData));
        return new String(cipherBytes, StandardCharsets.UTF_8);

    }
    
    // Encryption - Asymmetric
    
    public static byte[] encrypt(byte[] data, PublicKey publicKey) throws NoSuchPaddingException, 
    NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException {
    	
        Cipher cipher = Cipher.getInstance(CryptoConfiguration.ASYMMETRIC_ENCRYPTION_ALGO);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(data);
        
    }
    
    public static byte[] decrypt(byte[] data, PrivateKey privateKey) throws NoSuchPaddingException, 
    NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException {
    	
        Cipher cipher = Cipher.getInstance(CryptoConfiguration.ASYMMETRIC_ENCRYPTION_ALGO);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(data);
        
    }
    

    // Signatures

    public static String sign(PrivateKey privateKey, String message)
            throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {

        Signature signature = Signature.getInstance(CryptoConfiguration.SIGN_ALGO);
        signature.initSign(privateKey, generateSecureRandom());
        signature.update(message.getBytes());
        byte[] signatureBytes = signature.sign();
        return encodeBase64(signatureBytes);

    }

    public static boolean confirmSignature(PublicKey publicKey, String message, String signatureString)
            throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {

        Signature signature = Signature.getInstance(CryptoConfiguration.SIGN_ALGO);
        signature.initVerify(publicKey);
        signature.update(message.getBytes());
        return signature.verify(decodeBase64(signatureString));

    }

    // Base64

    public static String encodeBase64(byte[] dataArray) {
        return Base64.getEncoder().encodeToString(dataArray);
    }

    public static byte[] decodeBase64(String encodedData) {
        return Base64.getDecoder().decode(encodedData);
    }
    
    // Keys Management - Symmetric
    
    public static Key generateSecretKey() throws NoSuchAlgorithmException {
    	return AESKeyGenerator.generateSecretKey();
    }
    
    public static Key generateSecretKey(byte[] randomString) throws NoSuchAlgorithmException {
    	return AESKeyGenerator.generateSecretKey(randomString);
    }
    
    public static void generateSecretKeyToFile(String keyPath)
            throws NoSuchAlgorithmException, IOException {
    	AESKeyGenerator.generateSecretKeyToFile(keyPath);
    }
    
    public static Key readSecretKey(String keyPath) throws IOException {
    	return AESKeyGenerator.readSecretKey(keyPath);
    }
    
    /**
     *  Generate a shared key from random bytes
     */
    public static SecretKeySpec createSharedKeyFromString(byte[] randomBytes) {
        return new SecretKeySpec(randomBytes, 0, randomBytes.length, CryptoConfiguration.SYMMETRIC_ENCRYPTION_ALGO);
    }
    
    // Keys Management - Asymmetric
    
    public static KeyPair generateKeyPair() throws NoSuchAlgorithmException {
    	return RSAKeyGenerator.generateKeyPair();
    }
    
    public static void generateKeyPairToFile(String publicKeyPath, String privateKeyPath)
            throws NoSuchAlgorithmException, IOException {
    	RSAKeyGenerator.generateKeyPairToFile(publicKeyPath, privateKeyPath);
    }
    
    public static KeyPair readKeyPairFromFile(String publicKeyPath, String privateKeyPath)
            throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
    	return RSAKeyGenerator.readKeyPairFromFile(publicKeyPath, privateKeyPath);
    }

}
