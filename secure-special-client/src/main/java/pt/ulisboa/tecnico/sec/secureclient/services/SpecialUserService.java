package pt.ulisboa.tecnico.sec.secureclient.services;

import java.util.Arrays;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import pt.ulisboa.tecnico.sec.secureclient.SpecialClientApplication;
import pt.ulisboa.tecnico.sec.services.configs.PathConfiguration;
import pt.ulisboa.tecnico.sec.services.dto.ReportDTO;
import pt.ulisboa.tecnico.sec.services.dto.RequestLocationDTO;
import pt.ulisboa.tecnico.sec.services.dto.SecureDTO;
import pt.ulisboa.tecnico.sec.services.dto.SpecialUserResponseDTO;
import pt.ulisboa.tecnico.sec.services.exceptions.ApplicationException;
import pt.ulisboa.tecnico.sec.services.exceptions.UnreachableClientException;
import pt.ulisboa.tecnico.sec.services.interfaces.ISpecialUserService;
import pt.ulisboa.tecnico.sec.services.utils.crypto.CryptoService;
import pt.ulisboa.tecnico.sec.services.utils.crypto.CryptoUtils;

@Service
public class SpecialUserService implements ISpecialUserService {
	
	private static RestTemplate restTemplate = new RestTemplate();

	@Override
	public ReportDTO obtainLocationReport(String userIdSender, String userIdRequested, int epoch) throws ApplicationException {
		// Prepare the body of the HTTP request
        RequestLocationDTO req = new RequestLocationDTO();
        req.setUserIDSender(userIdSender);
        req.setUserIDRequested(userIdRequested);
        req.setEpoch(epoch);

        // Convert the above request body to a secure request object
        byte[] randomBytes = CryptoUtils.generateRandom32Bytes();
        SecureDTO secureDTO = CryptoService.generateNewSecureDTO(req, userIdSender, randomBytes);
        
        String urlAPI = PathConfiguration.GET_REPORT_URL;

        // Set HTTP headers
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

        // Send request and return the SecureDTO with the ReportDTO encapsulated
        HttpEntity<SecureDTO> entity = new HttpEntity<>(secureDTO, headers);
        ResponseEntity<SecureDTO> result = restTemplate.exchange(urlAPI, HttpMethod.POST, entity, SecureDTO.class);
        SecureDTO sec = result.getBody();

        // Check digital signature
        ReportDTO report = (ReportDTO) CryptoService.extractEncryptedData(sec, ReportDTO.class, CryptoUtils.createSharedKeyFromString(randomBytes));
        if(CryptoService.checkSecureDTODigitalSignature(sec, CryptoUtils.getServerPublicKey())) {
            return report;
        } else
            return null;
	}

    /**
     *      Special user normal behavior doesn't submit reports in the same way as a normal
     *      client. This functionality allows to create tests of normal behavior and byzantine behavior.
     *      It also allows to see the output in plaintext instead of seeing a secureDTO with encrypted
     *      information which wouldn't allow to extract any information of what happened.
     */
	@Override
	public void submitLocationReport(String userID, ReportDTO reportDTO) throws ApplicationException {
        System.out.println("\n[Special User] Report being sent:\n" + reportDTO.toString());

        byte[] randomBytes = CryptoUtils.generateRandom32Bytes();
        SecureDTO secureDTO = CryptoService.generateNewSecureDTO(reportDTO, userID, randomBytes);
        sendInfo(secureDTO, randomBytes);
	}

	public void sendInfo(SecureDTO secureDTO, byte[] randomBytes) throws UnreachableClientException {
        String urlAPI = PathConfiguration.SUBMIT_REPORT_URL;

        // Set HTTP req headers
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

        // Set HTTP req body
        HttpEntity<SecureDTO> entity = new HttpEntity<>(secureDTO, headers);

        try {
            // Send request & receive response
            ResponseEntity<SecureDTO> result = restTemplate.exchange(urlAPI, HttpMethod.POST, entity, SecureDTO.class);
            SecureDTO sec = result.getBody();
            CryptoService.extractEncryptedData(sec, String.class, CryptoUtils.createSharedKeyFromString(randomBytes));
        } catch(Exception e) {
            throw new UnreachableClientException("[Client "+ SpecialClientApplication.userId+"] Wasn't able to contact server.");
        }
    }

	@Override
	public SpecialUserResponseDTO obtainUsersAtLocation(String userId, int x, int y, int epoch)
			throws ApplicationException {

		RequestLocationDTO req = new RequestLocationDTO();
		req.setUserIDSender(userId);
		req.setEpoch(epoch);
		req.setX(x);
		req.setY(y);
		
		// Convert the above request body to a secure request object
        byte[] randomBytes = CryptoUtils.generateRandom32Bytes();
        SecureDTO secureDTO = CryptoService.generateNewSecureDTO(req, userId, randomBytes);
        
        String urlAPI = PathConfiguration.OBTAIN_USERS_AT_LOCATION_EPOCH;
        
        // Set HTTP headers
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        
        // Send request and return the SecureDTO with the ReportDTO encapsulated
        HttpEntity<SecureDTO> entity = new HttpEntity<>(secureDTO, headers);
        ResponseEntity<SecureDTO> result = restTemplate.exchange(urlAPI, HttpMethod.POST, entity, SecureDTO.class);
        SecureDTO sec = result.getBody();
        
        // Check digital signature
        SpecialUserResponseDTO response = (SpecialUserResponseDTO) CryptoService.extractEncryptedData(sec, SpecialUserResponseDTO.class, CryptoUtils.createSharedKeyFromString(randomBytes));
        if(CryptoService.checkSecureDTODigitalSignature(sec, CryptoUtils.getServerPublicKey())) {
            return response;
        } else
            return null;
	}

}
