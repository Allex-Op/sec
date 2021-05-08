package pt.ulisboa.tecnico.sec.secureclient.services;

import java.util.Arrays;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import pt.ulisboa.tecnico.sec.secureclient.ClientApplication;
import pt.ulisboa.tecnico.sec.services.configs.PathConfiguration;
import pt.ulisboa.tecnico.sec.services.dto.ClientResponseDTO;
import pt.ulisboa.tecnico.sec.services.dto.ReportDTO;
import pt.ulisboa.tecnico.sec.services.dto.RequestProofDTO;
import pt.ulisboa.tecnico.sec.services.dto.SecureDTO;
import pt.ulisboa.tecnico.sec.services.exceptions.ApplicationException;
import pt.ulisboa.tecnico.sec.services.exceptions.UnreachableClientException;
import pt.ulisboa.tecnico.sec.services.utils.crypto.CryptoService;
import pt.ulisboa.tecnico.sec.services.utils.crypto.CryptoUtils;

public class NetworkService {
	
	private static RestTemplate restTemplate = new RestTemplate();
	
	private NetworkService() {}
	
	public static <P, R> R sendMessageToServers(P unsecureDTO, Class<R> responseClass, String userIdSender, String endpoint) {
		// Convert the above request body to a secure request object
        byte[] randomBytes = CryptoUtils.generateRandom32Bytes();
        SecureDTO secureDTO = CryptoService.generateNewSecureDTO(unsecureDTO, userIdSender, randomBytes, "1");
        
        String url = PathConfiguration.buildUrl(PathConfiguration.getServerUrl(1), endpoint);
        
        SecureDTO sec = sendMessageToServer(secureDTO, url);
        
        R unwrappedDTO = (R) CryptoService.extractEncryptedData(sec, responseClass, CryptoUtils.createSharedKeyFromString(randomBytes));
        
        // Verify if conversion was successful and its a response dto
        if (unwrappedDTO == null || !CryptoService.checkSecureDTODigitalSignature(sec, CryptoUtils.getServerPublicKey("1")))
            return null;
        
        return unwrappedDTO;
	}
	
	public static <P> void sendMessageToServersWithoutResponse(P unsecureDTO, String userIdSender, String endpoint) throws ApplicationException {
		// Convert the above request body to a secure request object
        byte[] randomBytes = CryptoUtils.generateRandom32Bytes();
        SecureDTO secureDTO = CryptoService.generateNewSecureDTO(unsecureDTO, userIdSender, randomBytes, "1");
        
        String url = PathConfiguration.buildUrl(PathConfiguration.getServerUrl(1), endpoint);

        try {
            SecureDTO sec = sendMessageToServer(secureDTO, url);
            CryptoService.extractEncryptedData(sec, String.class, CryptoUtils.createSharedKeyFromString(randomBytes));
        } catch(Exception e) {
            throw new UnreachableClientException("[Client "+ ClientApplication.userId+"] Wasn't able to contact server.");
        }
	}
	
	private static SecureDTO sendMessageToServer(SecureDTO message, String url) {
		// Set HTTP headers
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        
        // Send request and return the SecureDTO with the ReportDTO encapsulated
        HttpEntity<SecureDTO> entity = new HttpEntity<>(message, headers);
        ResponseEntity<SecureDTO> result = restTemplate.exchange(url, HttpMethod.POST, entity, SecureDTO.class);
        return result.getBody();
	}
	
	public static ClientResponseDTO sendMessageToClient(RequestProofDTO request, String url) {
		// Set HTTP headers
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        
        HttpEntity<RequestProofDTO> entity = new HttpEntity<>(request, headers);
        ResponseEntity<ClientResponseDTO> result = restTemplate.exchange(url, HttpMethod.POST, entity, ClientResponseDTO.class);
        return result.getBody();
	}

}
