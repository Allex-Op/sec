package pt.ulisboa.tecnico.sec.secureclient.services;

import java.util.Arrays;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.sec.secureclient.ClientApplication;
import pt.ulisboa.tecnico.sec.services.dto.ClientResponseDTO;
import pt.ulisboa.tecnico.sec.services.dto.ProofDTO;
import pt.ulisboa.tecnico.sec.services.dto.RequestProofDTO;
import pt.ulisboa.tecnico.sec.services.interfaces.ILocationProofService;

@Service
public class LocationProofService implements ILocationProofService {
	
	private static List<String> nonces = new ArrayList<>();
	
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

		// Replay attack verification
		if(nonces.contains(clientResponse.getNonce())) {
			System.out.println("[Client " + ClientApplication.userId + "] Error occurred asking for proof: Replay Attack - nonce repeated.");
			return null;
		}
		nonces.add(clientResponse.getNonce());
		
		// Verify check digital signature
		if (!CryptoService.checkDigitalSignature(CryptoService.buildClientResponseMessage(clientResponse), clientResponse.getDigitalSignature(), CryptoUtils.getClientPublicKey(clientResponse.getProof().getUserID()))) {
			System.out.println("Can't validate request proof signature.");
			return null;
		}
		
		return clientResponse.getProof();
	}

}
