package pt.ulisboa.tecnico.sec.secureclient.services;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
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

import java.util.Arrays;
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

    	return NetworkService.sendMessageToServers(req, ReportDTO.class, userIdSender, PathConfiguration.GET_REPORT_ENDPOINT);
    }

    /**
     *  Submits a location report to the server
     */
    @Override
    public void submitLocationReport(String userID, ReportDTO reportDTO) throws ApplicationException {
    	NetworkService.sendMessageToServersWithoutResponse(reportDTO, userID, PathConfiguration.SUBMIT_REPORT_ENDPOINT);
    }

    /**
     *  Requests user issued proofs
     */
	@Override
	public ResponseUserProofsDTO requestMyProofs(String userIdSender, String userIdRequested, List<Integer> epochs)
			throws ApplicationException {
		RequestUserProofsDTO requestUserProofsDTO = new RequestUserProofsDTO();
		requestUserProofsDTO.setUserIdSender(userIdSender);
		requestUserProofsDTO.setUserIdRequested(userIdRequested);
		requestUserProofsDTO.setEpochs(epochs);

	    return ByzantineRegularRegisterService.readFromRegisters(requestUserProofsDTO, ResponseUserProofsDTO.class, userIdSender, PathConfiguration.GET_PROOFS_AT_EPOCHS_ENDPOINT);
	}


    /**
     *  Auxiliary function to build a request location DTO
     */
    private RequestLocationDTO buildRequestLocation(String userIdSender, String userIdRequested, int epoch) {
        String reqId = userIdRequested;

        RequestLocationDTO req = new RequestLocationDTO();
        req.setUserIDSender(userIdSender);
        req.setUserIDRequested(reqId); // For the normal client this ID should be the same, it must be checked server-side
        req.setEpoch(epoch);

        return req;
    }
}
