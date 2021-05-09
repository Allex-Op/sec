package pt.ulisboa.tecnico.sec.secureserver.controllers;


import org.apache.catalina.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import pt.ulisboa.tecnico.sec.secureserver.ServerApplication;
import pt.ulisboa.tecnico.sec.secureserver.services.NetworkService;
import pt.ulisboa.tecnico.sec.secureserver.services.UserService;
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
			System.out.println("\n[SERVER " + ServerApplication.serverId + "] Received obtain report request at server epoch:" + ServerApplication.epoch);
			RequestLocationDTO req = (RequestLocationDTO) CryptoService.extractEncryptedData(sec, RequestLocationDTO.class, ServerApplication.serverId);

			if (req == null)
				throw new ApplicationException("SecureDTO object was corrupt or malformed, was not possible to extract the information.");
			verifyRequestSignatureAndNonce(sec, req.getUserIDSender(), "/getReport");

			// Double-Echo Broadcast
			RequestDTO requestDTO = new RequestDTO();
			requestDTO.setRequestLocationDTO(req);
			requestDTO.setClientId(req.getUserIDSender());
			NetworkService.sendBroadcast(requestDTO);

			ReportDTO report = this.userService.obtainLocationReport(req.getUserIDSender(), req.getUserIDRequested(), req.getEpoch());
			System.out.println("[SERVER " + ServerApplication.serverId + "] Requested report was:"+report.toString());
			return CryptoService.generateResponseSecureDTO(sec, report, ServerApplication.serverId); // Mais a frente quando houver vários servers esta função tera que ter server ID
		} catch(ApplicationException e) {
			System.out.println("\n[SERVER " + ServerApplication.serverId + "] Exception caught, rethrowing for ExceptionHandler.");
			SecretKey sk = CryptoService.getSecretKeyFromDTO(sec, ServerApplication.serverId);

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
			RequestLocationDTO req = (RequestLocationDTO) CryptoService.extractEncryptedData(sec, RequestLocationDTO.class, ServerApplication.serverId);

			if (req == null)
				throw new ApplicationException("SecureDTO object was corrupt or malformed, was not possible to extract the information.");
			verifyRequestSignatureAndNonce(sec, req.getUserIDSender(), "/locations/management/");

			// Double-Echo Broadcast
			RequestDTO requestDTO = new RequestDTO();
			requestDTO.setRequestLocationDTO(req);
			requestDTO.setClientId(req.getUserIDSender());
			NetworkService.sendBroadcast(requestDTO);

			SpecialUserResponseDTO result = this.userService.obtainUsersAtLocation(req.getUserIDSender(), req.getX(), req.getY(), req.getEpoch());
			return CryptoService.generateResponseSecureDTO(sec, result, ServerApplication.serverId);
		} catch(ApplicationException e) {
			System.out.println("\n[SERVER " + ServerApplication.serverId + "] Exception caught, rethrowing for ExceptionHandler.");
			SecretKey sk = CryptoService.getSecretKeyFromDTO(sec, ServerApplication.serverId);

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
			System.out.println("\n[SERVER" + ServerApplication.serverId + "] Received submit report request at server epoch:" + ServerApplication.epoch);
			ReportDTO report = (ReportDTO) CryptoService.extractEncryptedData(sec, ReportDTO.class, ServerApplication.serverId);
			if (report == null)
				throw new ApplicationException("[SERVER " + ServerApplication.serverId + "] SecureDTO object was corrupt or malformed, was not possible to extract the information.");

			String clientId = report.getRequestProofDTO().getUserID();
			verifyRequestSignatureAndNonce(sec, clientId, "/submitReport");

			// Double-Echo Broadcast
			RequestDTO requestDTO = new RequestDTO();
			requestDTO.setReportDTO(report);
			requestDTO.setClientId(report.getRequestProofDTO().getUserID());
			NetworkService.sendBroadcast(requestDTO);

			// Submit report
			this.userService.submitLocationReport(report.getRequestProofDTO().getUserID(), report);

			// Report submitted, return to client
			System.out.println("[SERVER " + ServerApplication.serverId + "] Report submitted successfully for client " + clientId);
			return CryptoService.generateResponseSecureDTO(sec, "Report submitted successfully.", ServerApplication.serverId); // Mais a frente quando houver vários servers esta função tera que ter server ID
		} catch(ApplicationException e) {
			System.out.println("\n[SERVER " + ServerApplication.serverId + "] Exception caught, rethrowing for ExceptionHandler.");
			SecretKey sk = CryptoService.getSecretKeyFromDTO(sec, ServerApplication.serverId);

			if(sk == null)
				return null;

			e.setSecretKey(sk);
			throw e;
		}
	}
	
	@PostMapping(PathConfiguration.GET_PROOFS_AT_EPOCHS_ENDPOINT)
	public SecureDTO requestMyProofs(@RequestBody SecureDTO sec) throws ApplicationException {
		try {
			System.out.println("\n[SERVER" + ServerApplication.serverId + "] Received my request proofs request at server epoch:" + ServerApplication.epoch);
			RequestUserProofsDTO requestUserProofs = (RequestUserProofsDTO) CryptoService.extractEncryptedData(sec, RequestUserProofsDTO.class, ServerApplication.serverId);
			if (requestUserProofs == null)
				throw new ApplicationException("[SERVER " + ServerApplication.serverId + "] SecureDTO object was corrupt or malformed, was not possible to extract the information.");
	
			String clientIdSender = requestUserProofs.getUserIdSender();
			verifyRequestSignatureAndNonce(sec, clientIdSender, "/submitReport");

			// Double-Echo Broadcast
			RequestDTO requestDTO = new RequestDTO();
			requestDTO.setRequestUserProofsDTO(requestUserProofs);
			requestDTO.setClientId(requestUserProofs.getUserIdSender());
			NetworkService.sendBroadcast(requestDTO);

			ResponseUserProofsDTO result = this.userService.requestMyProofs(clientIdSender, requestUserProofs.getUserIdRequested(), requestUserProofs.getEpochs());
			return CryptoService.generateResponseSecureDTO(sec, result, ServerApplication.serverId);
		} catch(ApplicationException e) {
			System.out.println("\n[SERVER " + ServerApplication.serverId + "] Exception caught, rethrowing for ExceptionHandler.");
			SecretKey sk = CryptoService.getSecretKeyFromDTO(sec, ServerApplication.serverId);

			if(sk == null)
				return null;

			e.setSecretKey(sk);
			throw e;
		}
	}

	/**
	 * Echo message from Double-Echo Broadcast Algorithm
	 */
	@PostMapping(PathConfiguration.SERVER_ECHO)
	public void echo(@RequestBody SecureDTO secureDTO) throws ApplicationException {
		RequestDTO requestDTO = (RequestDTO) CryptoService.extractEncryptedData(secureDTO, RequestDTO.class, ServerApplication.serverId);
		if (requestDTO == null)
			throw new ApplicationException("[SERVER " + ServerApplication.serverId + "] SecureDTO object was corrupt or malformed, was not possible to extract the information.");
		System.out.println("\n[SERVER" + ServerApplication.serverId + "] Received echo from: " + requestDTO.getClientId());

		networkService.echo(requestDTO);
	}

	/**
	 * Ready message from Double-Echo Broadcast Algorithm
	 */
	@PostMapping(PathConfiguration.SERVER_READY)
	public void ready(@RequestBody SecureDTO secureDTO) throws ApplicationException {
		RequestDTO requestDTO = (RequestDTO) CryptoService.extractEncryptedData(secureDTO, RequestDTO.class, ServerApplication.serverId);
		if (requestDTO == null)
			throw new ApplicationException("[SERVER " + ServerApplication.serverId + "] SecureDTO object was corrupt or malformed, was not possible to extract the information.");
		System.out.println("\n[SERVER" + ServerApplication.serverId + "] Received ready from: " + requestDTO.getClientId());

		networkService.ready(requestDTO);
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
