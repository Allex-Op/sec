package pt.ulisboa.tecnico.sec.secureserver.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import pt.ulisboa.tecnico.sec.secureserver.ServerApplication;
import pt.ulisboa.tecnico.sec.secureserver.services.*;
import pt.ulisboa.tecnico.sec.services.configs.PathConfiguration;
import pt.ulisboa.tecnico.sec.services.dto.*;
import pt.ulisboa.tecnico.sec.services.exceptions.ApplicationException;
import pt.ulisboa.tecnico.sec.services.exceptions.SignatureCheckFailedException;
import pt.ulisboa.tecnico.sec.services.interfaces.ISpecialUserService;
import pt.ulisboa.tecnico.sec.services.utils.crypto.CryptoService;
import pt.ulisboa.tecnico.sec.services.utils.crypto.CryptoUtils;

import javax.crypto.SecretKey;

@RestController
public class UserController {
	
	private ISpecialUserService userService;
	private NetworkService networkService;

	@Autowired
	public UserController(ISpecialUserService userService, NetworkService networkService) {
		this.userService = userService;
		this.networkService = networkService;
	}

	/**
	 *	User queries server for a location report
	 */
	@PostMapping(PathConfiguration.GET_REPORT_ENDPOINT)
	public SecureDTO obtainLocationReport(@RequestBody SecureDTO sec) throws ApplicationException {
		try {
			verifyProofOfWork(sec);

			System.out.println("\n[SERVER " + ServerApplication.serverId + "] Received obtain report request.");
			RequestLocationDTO req = (RequestLocationDTO) CryptoService.serverExtractEncryptedData(sec, RequestLocationDTO.class, ServerApplication.serverId);

			if (req == null)
				throw new ApplicationException("SecureDTO object was corrupt or malformed, was not possible to extract the information.");
			verifyRequestSignatureAndNonce(sec, req.getUserIDSender(), PathConfiguration.GET_REPORT_ENDPOINT);

			// Executes the "Double Echo Broadcast" protocol to guarantee that all machines achieve the same status
			startDoubleEcho(req);

			// Read report
			ReportDTO report = ByzantineAtomicRegisterService.receiveReadRequest(req, sec.getRid(), (UserService) userService);

			System.out.println("[SERVER " + ServerApplication.serverId + "] Requested report was:"+report.toString());
			return CryptoService.generateResponseSecureDTO(sec, report, ServerApplication.serverId); // Mais a frente quando houver vários servers esta função tera que ter server ID
		} catch(ApplicationException e) {
			System.out.println("\n[SERVER " + ServerApplication.serverId + "] Exception caught, rethrowing for ExceptionHandler.");
			SecretKey sk = CryptoService.getServerSecretKeyFromDTO(sec, ServerApplication.serverId);

			if(sk == null)
				return null;

			e.setSecretKey(sk);
			throw e;
		}
	}

	/**
	 *	User submits location report to server
	 */
	@PostMapping(PathConfiguration.SUBMIT_REPORT_ENDPOINT)
	public SecureDTO submitLocationReport(@RequestBody SecureDTO sec) throws ApplicationException {
		try {
			verifyProofOfWork(sec);

			System.out.println("\n[SERVER" + ServerApplication.serverId + "] Received submit report request.");
			ReportDTO report = (ReportDTO) CryptoService.serverExtractEncryptedData(sec, ReportDTO.class, ServerApplication.serverId);
			if (report == null)
				throw new ApplicationException("[SERVER " + ServerApplication.serverId + "] SecureDTO object was corrupt or malformed, was not possible to extract the information.");

			String clientId = report.getRequestProofDTO().getUserID();
			verifyRequestSignatureAndNonce(sec, clientId, PathConfiguration.SUBMIT_REPORT_ENDPOINT);

			// Executes the "Double Echo Broadcast" protocol to guarantee that all machines achieve the same status
			startDoubleEcho(report);

			// Submit report
			AcknowledgeDto ack = ByzantineAtomicRegisterService.receiveWriteRequest(clientId, report, sec.getTimestamp(), (UserService) userService);

			// Report submitted, return to client
			System.out.println("[SERVER " + ServerApplication.serverId + "] Report submitted successfully for client " + clientId);
			return CryptoService.generateResponseSecureDTO(sec, ack, ServerApplication.serverId);
		} catch(ApplicationException e) {
			System.out.println("\n[SERVER " + ServerApplication.serverId + "] Exception caught, rethrowing for ExceptionHandler.");
			SecretKey sk = CryptoService.getServerSecretKeyFromDTO(sec, ServerApplication.serverId);

			if(sk == null)
				return null;

			e.setSecretKey(sk);
			throw e;
		}
	}


	/**
	 *	HA user asks for classified info for all users
	 */
	@PostMapping(PathConfiguration.OBTAIN_USERS_AT_LOCATION_EPOCH_ENDPOINT)
	public SecureDTO obtainUsersAtLocation(@RequestBody SecureDTO sec) throws ApplicationException {
		try {
			verifyProofOfWork(sec);

			RequestLocationDTO req = (RequestLocationDTO) CryptoService.serverExtractEncryptedData(sec, RequestLocationDTO.class, ServerApplication.serverId);

			if (req == null)
				throw new ApplicationException("SecureDTO object was corrupt or malformed, was not possible to extract the information.");
			verifyRequestSignatureAndNonce(sec, req.getUserIDSender(), PathConfiguration.OBTAIN_USERS_AT_LOCATION_EPOCH_ENDPOINT);

			// Executes the "Double Echo Broadcast" protocol to guarantee that all machines achieve the same status
			startDoubleEcho(req);

			// Reads from the register
			return ByzantineRegularRegisterService.receiveReadRequest(req, sec, userService);
		} catch(ApplicationException e) {
			System.out.println("\n[SERVER " + ServerApplication.serverId + "] Exception caught, rethrowing for ExceptionHandler.");
			SecretKey sk = CryptoService.getServerSecretKeyFromDTO(sec, ServerApplication.serverId);

			if(sk == null)
				return null;

			e.setSecretKey(sk);
			throw e;
		}
	}


	/**
	 *	User requests all the proofs he previously issued
	 */
	@PostMapping(PathConfiguration.GET_PROOFS_AT_EPOCHS_ENDPOINT)
	public SecureDTO requestMyProofs(@RequestBody SecureDTO sec) throws ApplicationException {
		try {
			verifyProofOfWork(sec);

			System.out.println("\n[SERVER" + ServerApplication.serverId + "] Received get client issued proofs request.");
			RequestUserProofsDTO requestUserProofs = (RequestUserProofsDTO) CryptoService.serverExtractEncryptedData(sec, RequestUserProofsDTO.class, ServerApplication.serverId);
			if (requestUserProofs == null)
				throw new ApplicationException("[SERVER " + ServerApplication.serverId + "] SecureDTO object was corrupt or malformed, was not possible to extract the information.");
	
			String clientIdSender = requestUserProofs.getUserIdSender();
			verifyRequestSignatureAndNonce(sec, clientIdSender, PathConfiguration.GET_PROOFS_AT_EPOCHS_ENDPOINT);

			// Executes the "Double Echo Broadcast" protocol to guarantee that all machines achieve the same status
			startDoubleEcho(requestUserProofs);

			// Reads from the register
			return ByzantineRegularRegisterService.receiveReadRequest(requestUserProofs, sec, userService);
		} catch(ApplicationException e) {
			System.out.println("\n[SERVER " + ServerApplication.serverId + "] Exception caught, rethrowing for ExceptionHandler.");
			SecretKey sk = CryptoService.getServerSecretKeyFromDTO(sec, ServerApplication.serverId);

			if(sk == null)
				return null;

			e.setSecretKey(sk);
			throw e;
		}
	}


	/***********************************************************************************************/
	/* 							Auxiliary Functions	- Double Echo Broadcast						   */
	/***********************************************************************************************/


	/**
	 * Start "Double Echo Broadcast" protocol for the request user proofs
	 */
	private void startDoubleEcho(RequestUserProofsDTO requestUserProofs) throws ApplicationException {
		// Double-Echo Broadcast
		RequestDTO requestDTO = new RequestDTO();
		requestDTO.setRequestUserProofsDTO(requestUserProofs);
		requestDTO.setClientId(requestUserProofs.getUserIdSender());
		requestDTO.setServerId(ServerApplication.serverId);
		NetworkService.sendBroadcast(requestDTO);
	}


	/**
	 * Start "Double Echo Broadcast" protocol for the request report DTO
	 */
	private void startDoubleEcho(ReportDTO report) throws ApplicationException {
		RequestDTO requestDTO = new RequestDTO();
		requestDTO.setReportDTO(report);
		requestDTO.setClientId(report.getRequestProofDTO().getUserID());
		requestDTO.setServerId(ServerApplication.serverId);
		NetworkService.sendBroadcast(requestDTO);
	}

	/**
	 * Start "Double Echo Broadcast" protocol for the request location DTO
	 */
	private void startDoubleEcho(RequestLocationDTO req) throws ApplicationException {
		// Double-Echo Broadcast
		RequestDTO requestDTO = new RequestDTO();
		requestDTO.setRequestLocationDTO(req);
		requestDTO.setClientId(req.getUserIDSender());
		requestDTO.setServerId(ServerApplication.serverId);
		NetworkService.sendBroadcast(requestDTO);
	}

	/**
	 * Received ECHO message during the "Double Echo Broadcast" protocol, calls
	 * NetworkService to add it to the data structures.
	 */
	@PostMapping(PathConfiguration.SERVER_ECHO)
	public void echo(@RequestBody SecureDTO secureDTO) throws ApplicationException {
		RequestDTO requestDTO = (RequestDTO) CryptoService.serverExtractEncryptedData(secureDTO, RequestDTO.class, ServerApplication.serverId);
		if (requestDTO == null)
			throw new ApplicationException("[SERVER " + ServerApplication.serverId + "] SecureDTO object was corrupt or malformed, was not possible to extract the information.");
		System.out.println("\n[SERVER" + ServerApplication.serverId + "] Received an echo from " + requestDTO.getServerId());

		networkService.echo(requestDTO);
	}

	/**
	 * Received READY message during the "Double Echo Broadcast" protocol, calls
	 * NetworkService to add it to the data structures.
	 */
	@PostMapping(PathConfiguration.SERVER_READY)
	public void ready(@RequestBody SecureDTO secureDTO) throws ApplicationException {
		RequestDTO requestDTO = (RequestDTO) CryptoService.serverExtractEncryptedData(secureDTO, RequestDTO.class, ServerApplication.serverId);
		if (requestDTO == null)
			throw new ApplicationException("[SERVER " + ServerApplication.serverId + "] SecureDTO object was corrupt or malformed, was not possible to extract the information.");
		System.out.println("\n[SERVER" + ServerApplication.serverId + "] Received a ready from " + requestDTO.getServerId());

		networkService.ready(requestDTO);
	}


	/***********************************************************************************************/
	/* 						Auxiliary Functions	- Byzantine Atomic Register						   */
	/***********************************************************************************************/

	@PostMapping(PathConfiguration.READ_COMPLETE_ENDPOINT)
	public void readComplete(@RequestBody SecureDTO secureDTO) throws ApplicationException {
		ReadCompleteDTO readCompleteDTO = (ReadCompleteDTO) CryptoService.serverExtractEncryptedData(secureDTO, ReadCompleteDTO.class, ServerApplication.serverId);
		if(readCompleteDTO == null)
			throw new ApplicationException("[SERVER " + ServerApplication.serverId + "] SecureDTO object was corrupt or malformed, was not possible to extract the information.");
		System.out.println("\n[SERVER" + ServerApplication.serverId + "] Received a READ COMPLETE from " + readCompleteDTO.getClientId());

		ByzantineAtomicRegisterService.readCompleteReceived(readCompleteDTO);
	}



	/***********************************************************************************************/
	/* 							Auxiliary Functions	- Secure Channels							   */
	/***********************************************************************************************/

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


	private void verifyProofOfWork(SecureDTO sec) throws ApplicationException {
		// Verify if the solution is correct
		if(!ProofOfWorkService.verifySolution(sec)) {
			System.out.println("[Server id: " + ServerApplication.serverId+"] Proof of work failed.");
			throw new ApplicationException("Invalid proof of work.");
		} else {
			System.out.println("[Server id: " + ServerApplication.serverId+"] Proof of work valid.");
		}
	}
}
