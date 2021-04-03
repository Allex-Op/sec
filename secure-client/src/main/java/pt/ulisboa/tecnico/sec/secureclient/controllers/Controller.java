package pt.ulisboa.tecnico.sec.secureclient.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import pt.ulisboa.tecnico.sec.secureclient.ClientApplication;
import pt.ulisboa.tecnico.sec.secureclient.services.LocationProofService;
import pt.ulisboa.tecnico.sec.secureclient.services.UserService;
import pt.ulisboa.tecnico.sec.services.dto.DTOFactory;
import pt.ulisboa.tecnico.sec.services.dto.ProofDTO;
import pt.ulisboa.tecnico.sec.services.dto.ReportDTO;
import pt.ulisboa.tecnico.sec.services.dto.RequestProofDTO;

@RestController
public class Controller {

	@Autowired
	private UserService userService;

	@PostMapping("/proof")
	public ProofDTO requestLocationProof(@RequestBody RequestProofDTO request) {
		return DTOFactory.makeProofDTO(ClientApplication.epoch, ClientApplication.userId, request, "bbb");
	}

	@GetMapping("/locations/{epoch}")
	public ReportDTO requestLocationInformation(@PathVariable int epoch) {
		System.out.println("Sending report request for user "+ ClientApplication.userId + " at epoch" + epoch);
		return userService.obtainLocationReport(ClientApplication.userId, epoch);
	}
}
