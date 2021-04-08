package pt.ulisboa.tecnico.sec.secureserver.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import pt.ulisboa.tecnico.sec.services.dto.ReportDTO;
import pt.ulisboa.tecnico.sec.services.dto.RequestLocationDTO;
import pt.ulisboa.tecnico.sec.services.dto.SecureDTO;
import pt.ulisboa.tecnico.sec.services.dto.SpecialUserResponseDTO;
import pt.ulisboa.tecnico.sec.services.exceptions.ApplicationException;
import pt.ulisboa.tecnico.sec.services.exceptions.SignatureCheckFailedException;
import pt.ulisboa.tecnico.sec.services.interfaces.ISpecialUserService;
import pt.ulisboa.tecnico.sec.services.utils.crypto.CryptoService;
import pt.ulisboa.tecnico.sec.services.utils.crypto.CryptoUtils;

@RestController
public class UserController {
	
	private ISpecialUserService userService;

	@Autowired
	public UserController(ISpecialUserService userService) { this.userService = userService; }

	//TODO: Ler nonces e salvar para BD

	/**
	 *	User queries server for a location report
	 */
	@PostMapping("/getReport")
	public SecureDTO obtainLocationReport(@RequestBody SecureDTO sec) throws ApplicationException {
		System.out.println("[Debug] Obtaining report....");
		RequestLocationDTO req = (RequestLocationDTO) CryptoService.extractEncryptedData(sec, RequestLocationDTO.class);
		if(req == null)
			throw new ApplicationException("SecureDTO object was corrupt or malformed, was not possible to extract the information.");

		verifyRequestSignature(sec, req.getUserIDSender(), "/getReport");

		ReportDTO report = this.userService.obtainLocationReport(req.getUserIDSender(), req.getEpoch());
		return CryptoService.generateResponseSecureDTO(sec, report); // Mais a frente quando houver vários servers esta função tera que ter server ID
	}


	/**
	 *	HA user asks for classified info for all users
	 */
	@PostMapping("/locations/management/")
	public SecureDTO obtainUsersAtLocation(@RequestBody SecureDTO sec) throws ApplicationException {
		RequestLocationDTO req = (RequestLocationDTO) CryptoService.extractEncryptedData(sec, RequestLocationDTO.class);

		verifyRequestSignature(sec, req.getUserIDSender(), "/locations/management/");

		SpecialUserResponseDTO result = this.userService.obtainUsersAtLocation(req.getUserIDSender(), req.getX(), req.getY(), req.getEpoch());
		return CryptoService.generateResponseSecureDTO(sec, result);
		
	}

	/**
	 *	User submits location report to server
	 */
	@PostMapping("/submitReport")
	public void submitLocationReport(@RequestBody SecureDTO sec) throws ApplicationException {
		System.out.println("[Debug] Submitting report....");
		ReportDTO report = (ReportDTO) CryptoService.extractEncryptedData(sec, ReportDTO.class);
		if(report == null)
			throw new ApplicationException("SecureDTO object was corrupt or malformed, was not possible to extract the information.");

		verifyRequestSignature(sec, report.getRequestProofDTO().getUserID(), "/submitReport");
		
		this.userService.submitLocationReport(report.getRequestProofDTO().getUserID(), report);

	}
	
	/**
	 * Verifies if the signature of a client request is valid and if it is not throws a exception
	 */
	private void verifyRequestSignature(SecureDTO sec, String userId, String endpoint) throws SignatureCheckFailedException {
		if (!CryptoService.checkSecureDTODigitalSignature(sec, CryptoUtils.getClientPublicKey(userId))) {
			throw new SignatureCheckFailedException("Digital signature check at " + endpoint + " failed.");
		}
	}
	
}
