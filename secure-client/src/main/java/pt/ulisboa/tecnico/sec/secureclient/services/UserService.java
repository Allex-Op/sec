package pt.ulisboa.tecnico.sec.secureclient.services;

import org.springframework.stereotype.Service;
import pt.ulisboa.tecnico.sec.secureclient.ClientApplication;
import pt.ulisboa.tecnico.sec.services.configs.PathConfiguration;
import pt.ulisboa.tecnico.sec.services.dto.ReportDTO;
import pt.ulisboa.tecnico.sec.services.dto.RequestLocationDTO;
import pt.ulisboa.tecnico.sec.services.dto.RequestUserProofsDTO;
import pt.ulisboa.tecnico.sec.services.dto.ResponseUserProofsDTO;
import pt.ulisboa.tecnico.sec.services.dto.SecureDTO;
import pt.ulisboa.tecnico.sec.services.exceptions.ApplicationException;
import pt.ulisboa.tecnico.sec.services.exceptions.UnreachableClientException;
import pt.ulisboa.tecnico.sec.services.interfaces.IUserService;
import pt.ulisboa.tecnico.sec.services.utils.crypto.CryptoService;
import pt.ulisboa.tecnico.sec.services.utils.crypto.CryptoUtils;

import java.util.List;

@Service
public class UserService implements IUserService {

    /**
     *  Requests a location report of a certain user at a certain epoch
     */
    @Override
    public ReportDTO obtainLocationReport(String userIdSender, String userIdRequested, int epoch) {
        // Prepare the body of the HTTP request
        RequestLocationDTO req = buildRequestLocation(userIdSender, userIdRequested, epoch);

        // Convert the above request body to a secure request object
        byte[] randomBytes = CryptoUtils.generateRandom32Bytes();
        SecureDTO secureDTO = CryptoService.generateNewSecureDTO(req, userIdSender, randomBytes, "1");
        
        String urlAPI = PathConfiguration.getGetReportURL(1);

        SecureDTO sec = NetworkService.sendMessageToServers(secureDTO, urlAPI);

        // Check digital signature
        ReportDTO report = (ReportDTO) CryptoService.extractEncryptedData(sec, ReportDTO.class, CryptoUtils.createSharedKeyFromString(randomBytes));

        // Verify if conversion was successful and its a valid report
        if (report == null || report.getRequestProofDTO().getUserID() == null || !CryptoService.checkSecureDTODigitalSignature(sec, CryptoUtils.getServerPublicKey("1")))
            return null;
        return report;
    }

    private RequestLocationDTO buildRequestLocation(String userIdSender, String userIdRequested, int epoch) {
        String reqId = userIdRequested;

        RequestLocationDTO req = new RequestLocationDTO();
        req.setUserIDSender(userIdSender);
        req.setUserIDRequested(reqId); // For the normal client this ID should be the same, it must be checked server-side
        req.setEpoch(epoch);

        return req;
    }

    /**
     *  Submits a location report to the server
     */
    @Override
    public void submitLocationReport(String userID, ReportDTO reportDTO) throws ApplicationException {
    	byte[] randomBytes = CryptoUtils.generateRandom32Bytes();
    	SecureDTO secureDTO = CryptoService.generateNewSecureDTO(reportDTO, userID, randomBytes, "1");

        String urlAPI = PathConfiguration.getSubmitReportURL(1);

        try {
            SecureDTO sec = NetworkService.sendMessageToServers(secureDTO, urlAPI);
            CryptoService.extractEncryptedData(sec, String.class, CryptoUtils.createSharedKeyFromString(randomBytes));
        } catch(Exception e) {
            throw new UnreachableClientException("[Client "+ ClientApplication.userId+"] Wasn't able to contact server.");
        }
    }

	@Override
	public ResponseUserProofsDTO requestMyProofs(String userIdSender, String userIdRequested, List<Integer> epochs)
			throws ApplicationException {
		RequestUserProofsDTO requestUserProofsDTO = new RequestUserProofsDTO();
		requestUserProofsDTO.setUserIdSender(userIdSender);
		requestUserProofsDTO.setUserIdRequested(userIdRequested);
		requestUserProofsDTO.setEpochs(epochs);
		
		byte[] randomBytes = CryptoUtils.generateRandom32Bytes();
		SecureDTO secureDTO = CryptoService.generateNewSecureDTO(requestUserProofsDTO, userIdSender, randomBytes, "1");
		
		String urlAPI = PathConfiguration.getGetProofsAtEpochsURL(1);

        SecureDTO sec = NetworkService.sendMessageToServers(secureDTO, urlAPI);

        // Check digital signature
        ResponseUserProofsDTO response = (ResponseUserProofsDTO) CryptoService.extractEncryptedData(sec, ResponseUserProofsDTO.class, CryptoUtils.createSharedKeyFromString(randomBytes));

        // Verify if conversion was successfull and its a valid report
        if (response == null || !CryptoService.checkSecureDTODigitalSignature(sec, CryptoUtils.getServerPublicKey("1")))
            return null;
		return response;
	}
}
