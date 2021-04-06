package pt.ulisboa.tecnico.sec.secureclient.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pt.ulisboa.tecnico.sec.secureclient.ClientApplication;
import pt.ulisboa.tecnico.sec.services.configs.PathConfiguration;
import pt.ulisboa.tecnico.sec.services.dto.ReportDTO;
import pt.ulisboa.tecnico.sec.services.dto.RequestLocationDTO;
import pt.ulisboa.tecnico.sec.services.dto.SecureDTO;
import pt.ulisboa.tecnico.sec.services.exceptions.ApplicationException;
import pt.ulisboa.tecnico.sec.services.exceptions.ErrorReadingKey;
import pt.ulisboa.tecnico.sec.services.exceptions.SignatureCheckFailed;
import pt.ulisboa.tecnico.sec.services.interfaces.IUserService;
import pt.ulisboa.tecnico.sec.services.utils.crypto.CryptoService;
import pt.ulisboa.tecnico.sec.services.utils.crypto.CryptoUtils;

import javax.crypto.SecretKey;
import java.security.PrivateKey;
import java.util.Arrays;

@Service
public class UserService implements IUserService {

    private static RestTemplate restTemplate = new RestTemplate();

    /**
     *  Requests a location report of a certain user at a certain epoch
     */
    @Override
    public ReportDTO obtainLocationReport(String userID, int epoch) {
        String urlAPI = PathConfiguration.GET_REPORT_URL;

        // Set HTTP headers
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

        // Prepare the body of the HTTP request
        RequestLocationDTO req = new RequestLocationDTO();
        req.setUserID(userID);
        req.setEpoch(epoch);

        // Convert the above request body to a secure request object
        byte[] randomBytes = CryptoUtils.generateRandom32Bytes();
        SecretKey key = CryptoService.generateSecretKey(randomBytes);

        SecureDTO secDTO = CryptoService.createSecureDTO(
                req,
                key,
                CryptoService.encryptRandomBytes(randomBytes),
                CryptoUtils.getClientPrivateKey(ClientApplication.userId)
        );

        // Send request and return the SecureDTO with the ReportDTO encapsulated
        HttpEntity<SecureDTO> entity = new HttpEntity<>(secDTO, headers);
        ResponseEntity<SecureDTO> result = restTemplate.exchange(urlAPI, HttpMethod.POST, entity, SecureDTO.class);
        SecureDTO sec = result.getBody();

        // Check digital signature
        ReportDTO report = (ReportDTO) CryptoService.extractEncryptedData(sec, ReportDTO.class, key);
        if(CryptoService.checkSecureDTODigitalSignature(sec, CryptoUtils.getServerPublicKey())) {
            return report;
        } else
            return null;
    }

    /**
     *  Submits a location report to the server
     */
    @Override
    public void submitLocationReport(String userID, ReportDTO reportDTO) throws ApplicationException {
        // Build secure DTO
        byte[] randomBytes = CryptoUtils.generateRandom32Bytes();
        SecretKey key = CryptoService.generateSecretKey(randomBytes);
        SecureDTO secDTO = CryptoService.createSecureDTO(
                reportDTO,
                key,
                CryptoService.encryptRandomBytes(randomBytes),
                CryptoUtils.getClientPrivateKey(ClientApplication.userId)
        );

        String urlAPI = PathConfiguration.SUBMIT_REPORT_URL;

        // Set HTTP req headers
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

        // Set HTTP req body
        HttpEntity<SecureDTO> entity = new HttpEntity<>(secDTO, headers);

        // Send request
        restTemplate.exchange(urlAPI, HttpMethod.POST, entity, SecureDTO.class);
    }
}
