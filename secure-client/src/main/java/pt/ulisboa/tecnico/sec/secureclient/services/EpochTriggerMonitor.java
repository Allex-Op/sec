package pt.ulisboa.tecnico.sec.secureclient.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import pt.ulisboa.tecnico.sec.secureclient.ClientApplication;
import pt.ulisboa.tecnico.sec.services.configs.PathConfiguration;
import pt.ulisboa.tecnico.sec.services.dto.DTOFactory;
import pt.ulisboa.tecnico.sec.services.dto.ProofDTO;
import pt.ulisboa.tecnico.sec.services.dto.ReportDTO;
import pt.ulisboa.tecnico.sec.services.dto.RequestProofDTO;
import pt.ulisboa.tecnico.sec.services.exceptions.ApplicationException;
import pt.ulisboa.tecnico.sec.services.utils.Grid;

@Component
public class EpochTriggerMonitor {
	
	private UserService userService;
	
	private LocationProofService locationProofService;
	
	@Autowired
	public EpochTriggerMonitor(UserService userService, LocationProofService locationProofService) {
		this.userService = userService;
		this.locationProofService = locationProofService;
	}
	
	@Scheduled(fixedRate = 10000, initialDelay = 10000)
	public void publish() {
		int myId = Integer.parseInt(ClientApplication.userId);
		
		System.out.println("Going to grid...");
		int[] myLocation = Grid.getLocationOfUserAtEpoch(myId, ClientApplication.epoch);
		System.out.println("My location: (" + myLocation[0] + "," + myLocation[1] + ")");
		List<Integer> witnesses = Grid.getUsersInRangeAtEpoch(myId, ClientApplication.epoch, 1);
		System.out.println("My neighbors are:");
		witnesses.forEach(x -> System.out.println("Neighbor " + x));
		
		RequestProofDTO requestProofDTO = DTOFactory.makeRequestProofDTO(myLocation[0], myLocation[1], 
				ClientApplication.epoch, ClientApplication.userId, "aaa");
		
		List<ProofDTO> proofs = new ArrayList<>();
		for (int witness : witnesses) {
			String url = PathConfiguration.getClientURL(witness);
			System.out.println("Asking for proof at " + url);
			proofs.add(locationProofService.requestLocationProof(url, requestProofDTO));
		}
		
		ReportDTO reportDTO = DTOFactory.makeReportDTO(requestProofDTO, proofs);
		System.out.println("SENDING REPORT TO SERVER:\n" + reportDTO.toString());
		
		try {
			System.out.println("Submitting report to server...");
			userService.submitLocationReport(ClientApplication.userId, reportDTO);
			System.out.println("Report Submitted!\nTrying to obtain the Report...");
			ReportDTO reportResponse = userService.obtainLocationReport(ClientApplication.userId, ClientApplication.epoch);
			System.out.println("RECEIVERD:\n" + reportResponse.toString());
		} catch (ApplicationException e) {
			e.printStackTrace();
		}
		
		ClientApplication.incrementEpoch();
	}

}
