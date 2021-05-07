package pt.ulisboa.tecnico.sec.secureclient.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import pt.ulisboa.tecnico.sec.secureclient.ClientApplication;
import pt.ulisboa.tecnico.sec.secureclient.services.UserService;
import pt.ulisboa.tecnico.sec.services.configs.ByzantineConfigurations;
import pt.ulisboa.tecnico.sec.services.dto.*;
import pt.ulisboa.tecnico.sec.services.exceptions.ApplicationException;
import pt.ulisboa.tecnico.sec.services.exceptions.ProverOutOfRangeException;
import pt.ulisboa.tecnico.sec.services.exceptions.RepeatedNonceException;
import pt.ulisboa.tecnico.sec.services.exceptions.SignatureCheckFailedException;
import pt.ulisboa.tecnico.sec.services.utils.Grid;
import pt.ulisboa.tecnico.sec.services.utils.crypto.CryptoService;
import pt.ulisboa.tecnico.sec.services.utils.crypto.CryptoUtils;

import java.util.ArrayList;
import java.util.List;

@RestController
public class Controller {

	private List<String> nonces = new ArrayList<>();

	@Autowired
	private UserService userService;

	/**
	 *	Another client asked for a location proof
	 */
	@PostMapping("/proof")
	public ClientResponseDTO requestLocationProof(@RequestBody RequestProofDTO request) throws ApplicationException {
		System.out.println("\n[Client"+ClientApplication.userId+"] Received proof request");

		// Verify Nonce & Add it
		if(nonces.contains(request.getNonce()))
			throw new RepeatedNonceException("Request Proof Nonce repeated. (Replay Attack)");
		nonces.add(request.getNonce());

		// Verify check digital signature
		if (!CryptoService.checkDigitalSignature(CryptoService.buildRequestProofMessage(request), request.getDigitalSignature(), CryptoUtils.getClientPublicKey(request.getUserID()))) {
			throw new SignatureCheckFailedException("Can't validate request proof signature.");
		}

		// Check if the prover is in my range
		int proverId = Integer.parseInt(request.getUserID());
		List<Integer> usersNearby = Grid.getUsersInRangeAtEpoch(Integer.parseInt(ClientApplication.userId), ClientApplication.epoch, ByzantineConfigurations.RANGE);

		if(usersNearby.contains(proverId)) {
			ProofDTO proof = DTOFactory.makeProofDTO(ClientApplication.epoch, ClientApplication.userId, request, "");
			CryptoService.signProofDTO(proof);

			ClientResponseDTO clientResponse = new ClientResponseDTO();
			clientResponse.setProof(proof); // sends a Proof to a Witness
			clientResponse.setNonce(CryptoUtils.generateNonce());
			CryptoService.signClientResponse(clientResponse, CryptoUtils.getClientPrivateKey(ClientApplication.userId));

			return clientResponse;
		} else
			throw new ProverOutOfRangeException("[Client"+ClientApplication.userId+"] Prover is not in range, can't generate proof...");
	}

	/**
	 *	Client asks for its location report at a certain epoch
	 */
	@GetMapping("/locations/{epoch}")
	public ReportDTO requestLocationInformation(@PathVariable int epoch) {
		System.out.println("\n[Client"+ClientApplication.userId+"] Sending report request for user "+ ClientApplication.userId + " at epoch" + epoch);
		return userService.obtainLocationReport(ClientApplication.userId, ClientApplication.userId, epoch);
	}
}
