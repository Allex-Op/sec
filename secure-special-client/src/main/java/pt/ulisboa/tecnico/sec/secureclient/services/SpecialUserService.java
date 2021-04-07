package pt.ulisboa.tecnico.sec.secureclient.services;

import java.util.Arrays;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import pt.ulisboa.tecnico.sec.services.configs.PathConfiguration;
import pt.ulisboa.tecnico.sec.services.dto.ReportDTO;
import pt.ulisboa.tecnico.sec.services.dto.RequestLocationDTO;
import pt.ulisboa.tecnico.sec.services.dto.SecureDTO;
import pt.ulisboa.tecnico.sec.services.dto.SpecialUserResponseDTO;
import pt.ulisboa.tecnico.sec.services.exceptions.ApplicationException;
import pt.ulisboa.tecnico.sec.services.interfaces.ISpecialUserService;
import pt.ulisboa.tecnico.sec.services.utils.crypto.CryptoService;
import pt.ulisboa.tecnico.sec.services.utils.crypto.CryptoUtils;

@Service
public class SpecialUserService implements ISpecialUserService {
	
	private static RestTemplate restTemplate = new RestTemplate();

	@Override
	public ReportDTO obtainLocationReport(String userID, int epoch) throws ApplicationException {
		// Prepare the body of the HTTP request
        RequestLocationDTO req = new RequestLocationDTO();
        req.setUserID(userID);
        req.setEpoch(epoch);

        // Convert the above request body to a secure request object
        byte[] randomBytes = CryptoUtils.generateRandom32Bytes();
        SecureDTO secureDTO = CryptoService.generateNewSecureDTO(req, userID, randomBytes);
        
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

	@Override
	public void submitLocationReport(String userID, ReportDTO reportDTO) throws ApplicationException {
		// empty
	}

	@Override
	public SpecialUserResponseDTO obtainUsersAtLocation(String userId, int x, int y, int epoch)
			throws ApplicationException {

		RequestLocationDTO req = new RequestLocationDTO();
		req.setUserID(userId);
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
