package pt.ulisboa.tecnico.sec.secureclient.services;

import java.util.Arrays;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import pt.ulisboa.tecnico.sec.secureclient.ClientApplication;
import pt.ulisboa.tecnico.sec.services.dto.ClientResponseDTO;
import pt.ulisboa.tecnico.sec.services.dto.ProofDTO;
import pt.ulisboa.tecnico.sec.services.dto.RequestProofDTO;
import pt.ulisboa.tecnico.sec.services.interfaces.ILocationProofService;

@Service
public class LocationProofService implements ILocationProofService {
	
	private static RestTemplate restTemplate = new RestTemplate();

	@Override
	public ProofDTO requestLocationProof(String url, RequestProofDTO request) {
		HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        
        HttpEntity<RequestProofDTO> entity = new HttpEntity<>(request, headers);
        
        ResponseEntity<ClientResponseDTO> result = restTemplate.exchange(url, HttpMethod.POST, entity, ClientResponseDTO.class);
        ClientResponseDTO clientResponse = result.getBody();

        if(clientResponse != null && clientResponse.getErr() != null) {
			System.out.println("[Client " + ClientApplication.userId + "] Error occurred asking for proof: " + clientResponse.getErr().getDescription());
			return null;
		}

        return clientResponse.getProof();
	}

}
