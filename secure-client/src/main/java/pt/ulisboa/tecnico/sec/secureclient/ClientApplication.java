package pt.ulisboa.tecnico.sec.secureclient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import pt.ulisboa.tecnico.sec.secureclient.services.UserService;
import pt.ulisboa.tecnico.sec.services.dto.DTOFactory;
import pt.ulisboa.tecnico.sec.services.dto.ProofDTO;
import pt.ulisboa.tecnico.sec.services.dto.ReportDTO;
import pt.ulisboa.tecnico.sec.services.dto.RequestProofDTO;

import java.util.Arrays;

@SpringBootApplication
@EnableScheduling
public class ClientApplication {

	private UserService userService;
	
	public static String userId;
	public static int epoch = 1;
	
	@Autowired
	public ClientApplication(UserService userService) {
		this.userService = userService;
	}
	
	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(ClientApplication.class, args);
		
		if (args.length < 2) {
			System.out.println("Need 2 arguments: <port> <userId_integer>");
			SpringApplication.exit(context, () -> 0);
			return;
		}
		
		userId = args[1];
		
		// SpringApplication.run(ClientApplication.class, args);
	}
	
	public static void incrementEpoch() {
		epoch++;
	}
	
//	@Bean
//	public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
//		return args -> {
//			System.out.println("Client Submits a new Report");
//
//			String userID = "1";
//			System.out.println("I am " + userID + "\nTrying to submit a new report...");
//
//			// Test Report Creation
//			RequestProofDTO requestProofDTO = DTOFactory.makeRequestProofDTO(10, 2, 1, userID, "aaa");
//			ProofDTO proofDTO1 = DTOFactory.makeProofDTO(1, "2", requestProofDTO, "bbb");
//			ProofDTO proofDTO2 = DTOFactory.makeProofDTO(1, "3", requestProofDTO, "ccc");
//			ReportDTO reportDTO = DTOFactory.makeReportDTO(requestProofDTO, Arrays.asList(proofDTO1,proofDTO2));
//
//			userService.submitLocationReport(userID, reportDTO);
//
//			System.out.println("Report Submitted!\nTrying to obtain the Report...");
//
//			ReportDTO reportResponse = userService.obtainLocationReport(userID, 1);
//
//			System.out.println(reportResponse.toString());
//		};
//	}

}
