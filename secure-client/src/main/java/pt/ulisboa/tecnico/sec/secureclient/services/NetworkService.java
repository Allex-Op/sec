package pt.ulisboa.tecnico.sec.secureclient.services;

import java.util.Arrays;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import pt.ulisboa.tecnico.sec.services.dto.ClientResponseDTO;
import pt.ulisboa.tecnico.sec.services.dto.RequestProofDTO;
import pt.ulisboa.tecnico.sec.services.dto.SecureDTO;

public class NetworkService {
	
	private static RestTemplate restTemplate = new RestTemplate();
	
	private NetworkService() {}
	
	public static SecureDTO sendMessageToServers(SecureDTO message, String url) {
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
