package pt.ulisboa.tecnico.sec.secureserver.services;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class EpochTriggerMonitor {

	@Scheduled(fixedRate = 10000, initialDelay = 5000)
	public void publish() {
		System.out.println("HTTP -> Clients");
		System.out.println("HTTP -> Server");
	}


}
