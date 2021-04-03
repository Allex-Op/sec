package pt.ulisboa.tecnico.sec.secureserver.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import pt.ulisboa.tecnico.sec.services.dto.ReportDTO;
import pt.ulisboa.tecnico.sec.services.dto.SecureDTO;
import pt.ulisboa.tecnico.sec.services.exceptions.ApplicationException;
import pt.ulisboa.tecnico.sec.services.interfaces.ISpecialUserService;
import pt.ulisboa.tecnico.sec.services.utils.crypto.CryptoService;

@RestController
public class UserController {
	
	private ISpecialUserService userService;

	@Autowired
	public UserController(ISpecialUserService userService) { this.userService = userService; }

	/**
	 *	User asks for report
	 */
	@GetMapping("/locations/{userID}/{epoch}")
	public ReportDTO obtainLocationReport(@PathVariable String userID, @PathVariable int epoch) {
		return this.userService.obtainLocationReport(userID, epoch);
	}

	/**
	 *	User submits report
	 */
	@PostMapping("/locations/{userID}")
	public void submitLocationReport(@PathVariable String userID, @RequestBody SecureDTO sec) throws ApplicationException {
		ReportDTO report = (ReportDTO) CryptoService.extractEncryptedData(sec, ReportDTO.class);

		if(report != null)
			this.userService.submitLocationReport(userID, report);
		else
			System.out.println("ReportDTO was null, data extraction failed");
	}

	/**
	 *	HA user asks for classified info for all users
	 */
	@GetMapping("/locations/management/{x}/{y}/{epoch}")
	public List<String> obtainUsersAtLocation(@PathVariable int x, @PathVariable int y, @PathVariable int epoch) {
		return this.userService.obtainUsersAtLocation(x + "," + y, epoch);
	}
	
}
