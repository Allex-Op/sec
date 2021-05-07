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

import pt.ulisboa.tecnico.sec.secureclient.SpecialClientApplication;
import pt.ulisboa.tecnico.sec.services.configs.PathConfiguration;
import pt.ulisboa.tecnico.sec.services.dto.*;
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
		System.out.println("[Special Client "+ SpecialClientApplication.userId+"] I am user '" + userIdSender + "', asking for a Report of user '" + userIdRequested + " at epoch: " + epoch);
		
		// Prepare the body of the HTTP request
        RequestLocationDTO req = new RequestLocationDTO();
        req.setUserIDSender(userIdSender);
        req.setUserIDRequested(userIdRequested);
        req.setEpoch(epoch);

        // Convert the above request body to a secure request object
        byte[] randomBytes = CryptoUtils.generateRandom32Bytes();
        SecureDTO secureDTO = CryptoService.generateNewSecureDTO(req, userIdSender, randomBytes);
        return obtainInfo(secureDTO, randomBytes);
	}
	
	public ReportDTO obtainInfo(SecureDTO secureDTO, byte[] randomBytes) {
		String urlAPI = PathConfiguration.GET_REPORT_URL;

        SecureDTO sec = NetworkService.sendMessageToServers(secureDTO, urlAPI);

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
        System.out.println("\n[Special Client" + SpecialClientApplication.userId + "] Report being sent:\n" + reportDTO.toString());

        byte[] randomBytes = CryptoUtils.generateRandom32Bytes();
        SecureDTO secureDTO = CryptoService.generateNewSecureDTO(reportDTO, userID, randomBytes);
        sendInfo(secureDTO, randomBytes);
	}

	public void sendInfo(SecureDTO secureDTO, byte[] randomBytes) throws UnreachableClientException {
        String urlAPI = PathConfiguration.SUBMIT_REPORT_URL;

        try {
            SecureDTO sec = NetworkService.sendMessageToServers(secureDTO, urlAPI);
            CryptoService.extractEncryptedData(sec, String.class, CryptoUtils.createSharedKeyFromString(randomBytes));
        } catch(Exception e) {
            throw new UnreachableClientException("[Special Client "+ SpecialClientApplication.userId+"] Wasn't able to contact server.");
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

        SecureDTO sec = NetworkService.sendMessageToServers(secureDTO, urlAPI);
        
        // Check digital signature
        SpecialUserResponseDTO response = (SpecialUserResponseDTO) CryptoService.extractEncryptedData(sec, SpecialUserResponseDTO.class, CryptoUtils.createSharedKeyFromString(randomBytes));
        if(CryptoService.checkSecureDTODigitalSignature(sec, CryptoUtils.getServerPublicKey())) {
            return response;
        } else
            return null;
	}

    public ProofDTO requestLocationProof(String url, RequestProofDTO request) {

        ClientResponseDTO clientResponse = NetworkService.sendMessageToClient(request, url);

        if(clientResponse != null && clientResponse.getErr() != null) {
            System.out.println("[Special Client " + SpecialClientApplication.userId + "] Error occurred asking for proof: " + clientResponse.getErr().getDescription());
            return null;
        }

        return clientResponse.getProof();
    }

	@Override
	public ResponseUserProofsDTO requestMyProofs(String userIdSender, String userIdRequested, List<Integer> epochs)
			throws ApplicationException {
		RequestUserProofsDTO requestUserProofsDTO = new RequestUserProofsDTO();
		requestUserProofsDTO.setUserIdSender(userIdSender);
		requestUserProofsDTO.setUserIdRequested(userIdRequested);
		requestUserProofsDTO.setEpochs(epochs);
		
		byte[] randomBytes = CryptoUtils.generateRandom32Bytes();
		SecureDTO secureDTO = CryptoService.generateNewSecureDTO(requestUserProofsDTO, userIdSender, randomBytes);
		
		String urlAPI = PathConfiguration.GET_PROOFS_AT_EPOCHS;

        SecureDTO sec = NetworkService.sendMessageToServers(secureDTO, urlAPI);

        // Check digital signature
        ResponseUserProofsDTO response = (ResponseUserProofsDTO) CryptoService.extractEncryptedData(sec, ResponseUserProofsDTO.class, CryptoUtils.createSharedKeyFromString(randomBytes));

        // Verify if conversion was successfull and its a valid report
        if (response == null || !CryptoService.checkSecureDTODigitalSignature(sec, CryptoUtils.getServerPublicKey()))
            return null;
		return response;
	}

}
