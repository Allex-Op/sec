package pt.ulisboa.tecnico.sec.secureclient.controllers;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import pt.ulisboa.tecnico.sec.secureclient.ClientApplication;
import pt.ulisboa.tecnico.sec.services.dto.DTOFactory;
import pt.ulisboa.tecnico.sec.services.dto.ProofDTO;
import pt.ulisboa.tecnico.sec.services.dto.RequestProofDTO;

@RestController
public class Controller {
	
	@PostMapping("/proof")
	public ProofDTO requestLocationProof(@RequestBody RequestProofDTO request) {
		return DTOFactory.makeProofDTO(ClientApplication.epoch, ClientApplication.userId, request, "bbb");
	}

}
