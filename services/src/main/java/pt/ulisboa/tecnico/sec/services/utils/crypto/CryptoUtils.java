package pt.ulisboa.tecnico.sec.services.utils.crypto;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

public class CryptoUtils {

    // Key sizes
    protected static final int SYMMETRIC_KEY_SIZE = 256;
    protected static final int ASYMMETRIC_KEY_SIZE = 2048;

    // Algorithms
    protected static final String ASYMMETRIC_ENCRYPTION_ALGO = "RSA";
    protected static final String SYMMETRIC_ENCRYPTION_ALGO = "AES";
    private static final String CIPHER_ALGO = SYMMETRIC_ENCRYPTION_ALGO + "/GCM/PKCS5Padding";
    private static final String SIGN_ALGO = "SHA256withRSA";

    // Key Store Type
    private static final String KEYSTORE_TYPE = "JCEKS";
    private static KeyStore keyStore;

    private static void loadKeyStore(String keystorePath, String keystorePwd) {
        try (FileInputStream kfile = new FileInputStream(keystorePath)) {
            // Create an instance of KeyStore of type “JCEKS”
            keyStore = KeyStore.getInstance(KEYSTORE_TYPE);
            // Load the null Keystore and set  the password to “keystorePwd”
            keyStore.load(kfile, keystorePwd.toCharArray());
            KeyStore.SecretKeyEntry skEntry = new KeyStore.(mykey);
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
