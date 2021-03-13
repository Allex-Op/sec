package pt.ulisboa.tecnico.sec.secureclient.services;

import java.util.Arrays;
import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import pt.ulisboa.tecnico.sec.services.configs.PathConfiguration;
import pt.ulisboa.tecnico.sec.services.interfaces.IMessageService;

@Service
public class MessageService implements IMessageService {
	
	private static RestTemplate restTemplate = new RestTemplate();
	
	private MessageService() {}

	@Override
	public String echo(String msg) {
		HttpHeaders headers = new org.springframework.http.HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		
		HttpEntity<String> entity = new HttpEntity<>(msg, headers);
		
		ResponseEntity<String> result = restTemplate.exchange(PathConfiguration.MESSAGES_API, HttpMethod.POST, entity, String.class);
		return result.getBody();
	}

	@Override
	public List<String> getAllMessages() {
		// TODO Auto-generated method stub
		return null;
	}

}
