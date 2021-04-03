package pt.ulisboa.tecnico.sec.secureclient.services;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pt.ulisboa.tecnico.sec.services.configs.PathConfiguration;
import pt.ulisboa.tecnico.sec.services.dto.ReportDTO;
import pt.ulisboa.tecnico.sec.services.exceptions.ApplicationException;
import pt.ulisboa.tecnico.sec.services.interfaces.IUserService;

import java.util.Arrays;

@Service
public class UserService implements IUserService {

    private static RestTemplate restTemplate = new RestTemplate();


    @Override
    public ReportDTO obtainLocationReport(String userID, int epoch) {
        String urlAPI = PathConfiguration.USER_API+"/"+userID+"/"+epoch;

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.set("Authorization", "assinatura-digital-do-url");

        HttpEntity<ReportDTO> entity = new HttpEntity<>(null, headers);

        ResponseEntity<ReportDTO> result = restTemplate.exchange(urlAPI, HttpMethod.GET, entity, ReportDTO.class);
        return result.getBody();
    }

    @Override
    public void submitLocationReport(String userID, ReportDTO reportDTO) throws ApplicationException {
        String urlAPI = PathConfiguration.USER_API+"/"+userID;

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

        HttpEntity<ReportDTO> entity = new HttpEntity<>(reportDTO, headers);

        restTemplate.exchange(urlAPI, HttpMethod.POST, entity, ReportDTO.class);
    }
}
