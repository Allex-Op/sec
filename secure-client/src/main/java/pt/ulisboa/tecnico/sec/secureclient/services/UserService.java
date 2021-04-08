package pt.ulisboa.tecnico.sec.secureclient.services;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pt.ulisboa.tecnico.sec.services.configs.PathConfiguration;
import pt.ulisboa.tecnico.sec.services.dto.ReportDTO;
import pt.ulisboa.tecnico.sec.services.dto.RequestLocationDTO;
import pt.ulisboa.tecnico.sec.services.dto.SecureDTO;
import pt.ulisboa.tecnico.sec.services.exceptions.ApplicationException;
import pt.ulisboa.tecnico.sec.services.interfaces.IUserService;
import pt.ulisboa.tecnico.sec.services.utils.crypto.CryptoService;
import pt.ulisboa.tecnico.sec.services.utils.crypto.CryptoUtils;

import java.util.Arrays;

@Service
public class UserService implements IUserService {

    private static RestTemplate restTemplate = new RestTemplate();

    /**
     *  Requests a location report of a certain user at a certain epoch
     */
    @Override
    public ReportDTO obtainLocationReport(String userIdSender, String userIdRequested, int epoch) {
        // Prepare the body of the HTTP request
        RequestLocationDTO req = new RequestLocationDTO();
        req.setUserIDSender(userIdSender);
        req.setUserIDRequested(userIdRequested); // For the normal client this ID should be the same, it must be checked server-side
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

        // TODO: VERIFICAR STATUS CODE, SE DIFERENTE DE 200 ENTÃO FOI UM ERRORMESSAGE E DEVE
        // TODO: CONVERTER PARA TAL, OU O OBJETO REPORT VAI SER NULL E LANÇAR EXCEÇÃO NO CLIENTE
        if(report == null)
            System.out.println("Instead of a report it was returned an ErrorMessageDTO, temporary info, do planned changes.");

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
    	byte[] randomBytes = CryptoUtils.generateRandom32Bytes();
    	SecureDTO secureDTO = CryptoService.generateNewSecureDTO(reportDTO, userID, randomBytes);

        String urlAPI = PathConfiguration.SUBMIT_REPORT_URL;

        // Set HTTP req headers
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

        // Set HTTP req body
        HttpEntity<SecureDTO> entity = new HttpEntity<>(secureDTO, headers);

        // Send request
        restTemplate.exchange(urlAPI, HttpMethod.POST, entity, SecureDTO.class);
    }
}
