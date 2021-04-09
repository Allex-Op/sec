package pt.ulisboa.tecnico.sec.secureserver.services;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pt.ulisboa.tecnico.sec.secureserver.ServerApplication;

@Component
public class EpochTriggerMonitor {

	@Scheduled(fixedRate = 10000, initialDelay = 30000)
	public void publish() {
		//System.out.println("HTTP -> Clients");
		//System.out.println("HTTP -> Server");
		ServerApplication.incrementEpoch();
	}


}
