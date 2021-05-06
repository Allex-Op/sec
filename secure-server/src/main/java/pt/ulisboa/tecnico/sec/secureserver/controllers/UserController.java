package pt.ulisboa.tecnico.sec.secureserver.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import pt.ulisboa.tecnico.sec.secureserver.ServerApplication;
import pt.ulisboa.tecnico.sec.secureserver.services.UserService;
import pt.ulisboa.tecnico.sec.services.dto.ReportDTO;
import pt.ulisboa.tecnico.sec.services.dto.RequestLocationDTO;
import pt.ulisboa.tecnico.sec.services.dto.RequestUserProofsDTO;
import pt.ulisboa.tecnico.sec.services.dto.ResponseUserProofsDTO;
import pt.ulisboa.tecnico.sec.services.dto.SecureDTO;
import pt.ulisboa.tecnico.sec.services.dto.SpecialUserResponseDTO;
import pt.ulisboa.tecnico.sec.services.exceptions.ApplicationException;
import pt.ulisboa.tecnico.sec.services.exceptions.SignatureCheckFailedException;
import pt.ulisboa.tecnico.sec.services.interfaces.ISpecialUserService;
import pt.ulisboa.tecnico.sec.services.utils.crypto.CryptoService;
import pt.ulisboa.tecnico.sec.services.utils.crypto.CryptoUtils;

import javax.crypto.SecretKey;

@RestController
public class UserController {
	
	private ISpecialUserService userService;

	@Autowired
	public UserController(ISpecialUserService userService) { this.userService = userService; }

	/**
	 *	User queries server for a location report
	 */
	@PostMapping("/getReport")
	public SecureDTO obtainLocationReport(@RequestBody SecureDTO sec) throws ApplicationException {
		try {
			System.out.println("\n[SERVER " + ServerApplication.serverId + "] Received obtain report request at server epoch:" + ServerApplication.epoch);
			RequestLocationDTO req = (RequestLocationDTO) CryptoService.extractEncryptedData(sec, RequestLocationDTO.class);

			if (req == null)
				throw new ApplicationException("SecureDTO object was corrupt or malformed, was not possible to extract the information.");
			verifyRequestSignatureAndNonce(sec, req.getUserIDSender(), "/getReport");

			ReportDTO report = this.userService.obtainLocationReport(req.getUserIDSender(), req.getUserIDRequested(), req.getEpoch());
			System.out.println("[SERVER " + ServerApplication.serverId + "] Requested report was:"+report.toString());
			return CryptoService.generateResponseSecureDTO(sec, report); // Mais a frente quando houver vários servers esta função tera que ter server ID
		} catch(ApplicationException e) {
			System.out.println("\n[SERVER " + ServerApplication.serverId + "] Exception caught, rethrowing for ExceptionHandler.");
			SecretKey sk = CryptoService.getSecretKeyFromDTO(sec);

			if(sk == null)
				return null;

			e.setSecretKey(sk);
			throw e;
		}
	}

	/**
	 *	HA user asks for classified info for all users
	 */
	@PostMapping("/locations/management/")
	public SecureDTO obtainUsersAtLocation(@RequestBody SecureDTO sec) throws ApplicationException {
		try {
			RequestLocationDTO req = (RequestLocationDTO) CryptoService.extractEncryptedData(sec, RequestLocationDTO.class);

			verifyRequestSignatureAndNonce(sec, req.getUserIDSender(), "/locations/management/");

			SpecialUserResponseDTO result = this.userService.obtainUsersAtLocation(req.getUserIDSender(), req.getX(), req.getY(), req.getEpoch());
			return CryptoService.generateResponseSecureDTO(sec, result);
		} catch(ApplicationException e) {
			System.out.println("\n[SERVER " + ServerApplication.serverId + "] Exception caught, rethrowing for ExceptionHandler.");
			SecretKey sk = CryptoService.getSecretKeyFromDTO(sec);

			if(sk == null)
				return null;

			e.setSecretKey(sk);
			throw e;
		}
	}

	/**
	 *	User submits location report to server
	 */
	@PostMapping("/submitReport")
	public SecureDTO submitLocationReport(@RequestBody SecureDTO sec) throws ApplicationException {
		try {
			System.out.println("\n[SERVER" + ServerApplication.serverId + "] Received submit report request at server epoch:" + ServerApplication.epoch);
			ReportDTO report = (ReportDTO) CryptoService.extractEncryptedData(sec, ReportDTO.class);
			if (report == null)
				throw new ApplicationException("[SERVER " + ServerApplication.serverId + "] SecureDTO object was corrupt or malformed, was not possible to extract the information.");

			String clientId = report.getRequestProofDTO().getUserID();
			verifyRequestSignatureAndNonce(sec, clientId, "/submitReport");

			// Submit report
			this.userService.submitLocationReport(report.getRequestProofDTO().getUserID(), report);

			// Report submitted, return to client
			System.out.println("[SERVER " + ServerApplication.serverId + "] Report submitted successfully for client " + clientId);
			return CryptoService.generateResponseSecureDTO(sec, "Report submitted successfully."); // Mais a frente quando houver vários servers esta função tera que ter server ID
		} catch(ApplicationException e) {
			System.out.println("\n[SERVER " + ServerApplication.serverId + "] Exception caught, rethrowing for ExceptionHandler.");
			SecretKey sk = CryptoService.getSecretKeyFromDTO(sec);

			if(sk == null)
				return new SecureDTO();

			e.setSecretKey(sk);
			throw e;
		}
	}
	
	@PostMapping("/getProofs")
	public SecureDTO requestMyProofs(@RequestBody SecureDTO sec) throws ApplicationException {
		try {
			System.out.println("\n[SERVER" + ServerApplication.serverId + "] Received my request proofs request at server epoch:" + ServerApplication.epoch);
			RequestUserProofsDTO requestUserProofs = (RequestUserProofsDTO) CryptoService.extractEncryptedData(sec, RequestUserProofsDTO.class);
			if (requestUserProofs == null)
				throw new ApplicationException("[SERVER " + ServerApplication.serverId + "] SecureDTO object was corrupt or malformed, was not possible to extract the information.");
	
			String clientIdSender = requestUserProofs.getUserIdSender();
			verifyRequestSignatureAndNonce(sec, clientIdSender, "/submitReport");
			
			ResponseUserProofsDTO result = this.userService.requestMyProofs(clientIdSender, requestUserProofs.getUserIdRequested(), requestUserProofs.getEpochs());
			return CryptoService.generateResponseSecureDTO(sec, result);
		} catch(ApplicationException e) {
			System.out.println("\n[SERVER " + ServerApplication.serverId + "] Exception caught, rethrowing for ExceptionHandler.");
			SecretKey sk = CryptoService.getSecretKeyFromDTO(sec);

			if(sk == null)
				return null;

			e.setSecretKey(sk);
			throw e;
		}
	}
	
	/**
	 * Verifies if the signature of a client request is valid and if it is not throws a exception
	 */
	private void verifyRequestSignatureAndNonce(SecureDTO sec, String userId, String endpoint) throws ApplicationException {
		// Verifies the signature of the Secure DTO
		if (!CryptoService.checkSecureDTODigitalSignature(sec, CryptoUtils.getClientPublicKey(userId))) {
			throw new SignatureCheckFailedException("Digital signature check at " + endpoint + " failed.");
		}

		// Verifies if the nonce is repeated, if not adds it to the database to the according user.
		((UserService) this.userService).verifyNonce(userId, sec.getNonce());
	}
	
}
