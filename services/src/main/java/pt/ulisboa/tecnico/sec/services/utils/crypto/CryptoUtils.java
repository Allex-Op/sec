package pt.ulisboa.tecnico.sec.services.utils.crypto;

import pt.ulisboa.tecnico.sec.services.configs.CryptoConfiguration;

import javax.crypto.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.Base64;
import java.util.UUID;

public class CryptoUtils {

    // Key Store Type
    private static final String KEYSTORE_TYPE = "JCEKS";
    private static KeyStore keyStore;

    // Nonce and Secure Random

    public static String generateNonce() {
        return UUID.randomUUID().toString();
    }

    public static SecureRandom generateSecureRandom() {
        return new SecureRandom();
    }

    // Encryption

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

    // Keystore Management

    private static void loadKeyStore(String keystorePath, String keystorePwd) {
        try (FileInputStream kfile = new FileInputStream(keystorePath)) {
            // Create an instance of KeyStore of type “JCEKS”
            keyStore = KeyStore.getInstance(KEYSTORE_TYPE);
            // Load the null Keystore and set  the password to “keystorePwd”
            keyStore.load(kfile, keystorePwd.toCharArray());
        } catch (IOException e) {
            try {
                File newKsFile = new File(keystorePath);
                // If file already exists will do nothing
                newKsFile.createNewFile();
                keyStore = KeyStore.getInstance(KEYSTORE_TYPE);
                FileInputStream fis = new FileInputStream(newKsFile);
                // Write  the KeyStore into the file
                keyStore.load(fis, keystorePwd.toCharArray());
                // Close  the file  stream
                fis.close();
            } catch (IOException | KeyStoreException | NoSuchAlgorithmException | CertificateException ioException) {
                e.printStackTrace();
            }
        } catch(KeyStoreException | NoSuchAlgorithmException | CertificateException e2) {
            e2.printStackTrace();
        }
    }
}
