package pt.ulisboa.tecnico.sec.secureserver.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import pt.ulisboa.tecnico.sec.services.dto.ReportDTO;
import pt.ulisboa.tecnico.sec.services.dto.RequestLocationDTO;
import pt.ulisboa.tecnico.sec.services.dto.SecureDTO;
import pt.ulisboa.tecnico.sec.services.exceptions.ApplicationException;
import pt.ulisboa.tecnico.sec.services.interfaces.ISpecialUserService;
import pt.ulisboa.tecnico.sec.services.utils.crypto.CryptoService;
import pt.ulisboa.tecnico.sec.services.utils.crypto.CryptoUtils;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

@RestController
public class UserController {
	
	private ISpecialUserService userService;

	@Autowired
	public UserController(ISpecialUserService userService) { this.userService = userService; }

	/**
	 *	User queries server for a location report
	 */
	@PostMapping("/getReport")
	public SecureDTO obtainLocationReport(@RequestBody SecureDTO sec) {
		// Obtain report for the user x at epoch y and return secure response
		RequestLocationDTO req = (RequestLocationDTO) CryptoService.extractEncryptedData(
				sec,
				RequestLocationDTO.class,
				CryptoService.getSecretKeyFromDTO(sec)
		);

		// Check digital signature of the received packet, it must know the client id
		 SecureDTO secReport = null;
		if(CryptoService.checkSecureDTODigitalSignature(sec, CryptoUtils.getClientPublicKey(req.getUserID()))) {
			// Huh what? TODO: Check database instead
			ReportDTO report = this.userService.obtainLocationReport(req.getUserID(), req.getEpoch());

			// Return the response over secure channel
			secReport = CryptoService.createSecureDTO(
					report,
					CryptoService.getSecretKeyFromDTO(sec),
					"",
					CryptoUtils.getServerPrivateKey()    // Mais a frente quando houver vários servers esta função tera que ter server ID
			);
		} else {
			System.out.println("Digital signature check at /getReport failed.");
		}

		return secReport;
	}


	/**
	 *	HA user asks for classified info for all users
	 */
	@GetMapping("/locations/management/")
	public SecureDTO obtainUsersAtLocation(@RequestBody SecureDTO sec) {
		// Extract secure data
		RequestLocationDTO req = (RequestLocationDTO) CryptoService.extractEncryptedData(
				sec,
				RequestLocationDTO.class,
				CryptoService.getSecretKeyFromDTO(sec));

		// Check if the digitalSignature belongs to the special user
		throw new NotImplementedException();

		/*
		// Obtain witnesses and build the response
		List<String> info = this.userService.obtainUsersAtLocation(req.getX() + "," + req.getY(), req.getEpoch());
		SpecialUserResponseDTO resp = new SpecialUserResponseDTO();
		resp.setWitnesses(info);

		// Return the response over secure channel
		SecureDTO secInfo = CryptoService.createSecureDTO(resp);
		return secInfo;
		 */
	}

	/**
	 *	User submits location report to server
	 */
	@PostMapping("/submitReport")
	public void submitLocationReport(@RequestBody SecureDTO sec) throws ApplicationException {
		ReportDTO report = (ReportDTO) CryptoService.extractEncryptedData(
				sec,
				ReportDTO.class,
				CryptoService.getSecretKeyFromDTO(sec)
		);

		// Check digital signature of the received packet, it must know the client id
		if(CryptoService.checkSecureDTODigitalSignature(sec, CryptoUtils.getClientPublicKey(report.getRequestProofDTO().getUserID()))) {
			String userId = report.getRequestProofDTO().getUserID();
			this.userService.submitLocationReport(userId, report);
		} else
			System.out.println("Digital signature check at '/submitReport' failed.");

	}
	
}
