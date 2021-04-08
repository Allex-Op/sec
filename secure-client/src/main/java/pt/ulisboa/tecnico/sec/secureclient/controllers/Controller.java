package pt.ulisboa.tecnico.sec.secureclient.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import pt.ulisboa.tecnico.sec.secureclient.ClientApplication;
import pt.ulisboa.tecnico.sec.secureclient.services.UserService;
import pt.ulisboa.tecnico.sec.services.configs.ByzantineConfigurations;
import pt.ulisboa.tecnico.sec.services.dto.DTOFactory;
import pt.ulisboa.tecnico.sec.services.dto.ProofDTO;
import pt.ulisboa.tecnico.sec.services.dto.ReportDTO;
import pt.ulisboa.tecnico.sec.services.dto.RequestProofDTO;
import pt.ulisboa.tecnico.sec.services.exceptions.ProverOutOfRangeException;
import pt.ulisboa.tecnico.sec.services.utils.Grid;

import java.util.List;

@RestController
public class Controller {

	@Autowired
	private UserService userService;

	/**
	 *	Another client asked for a location proof
	 */
	@PostMapping("/proof")
	public ProofDTO requestLocationProof(@RequestBody RequestProofDTO request) throws ProverOutOfRangeException {
		System.out.println("Received proof request");

		// Check if the prover is in my range
		int proverId = Integer.parseInt(request.getUserID());
		List<Integer> usersNearby = Grid.getUsersInRangeAtEpoch(Integer.parseInt(ClientApplication.userId), ClientApplication.epoch, ByzantineConfigurations.RANGE);

		if(usersNearby.contains(proverId))
			return DTOFactory.makeProofDTO(ClientApplication.epoch, ClientApplication.userId, request, "bbb");
		else
			throw new ProverOutOfRangeException("Prover is not in range, can't generate proof...");
	}

	/**
	 *	Client asks for its location report at a certain epoch
	 */
	@GetMapping("/locations/{epoch}")
	public ReportDTO requestLocationInformation(@PathVariable int epoch) {
		System.out.println("Sending report request for user "+ ClientApplication.userId + " at epoch" + epoch);
		return userService.obtainLocationReport(ClientApplication.userId, ClientApplication.userId, epoch);
	}
}
