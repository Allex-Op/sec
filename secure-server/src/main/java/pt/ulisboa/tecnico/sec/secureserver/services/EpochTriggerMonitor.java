package pt.ulisboa.tecnico.sec.secureserver.services;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pt.ulisboa.tecnico.sec.secureserver.ServerApplication;

import java.sql.Timestamp;
import java.util.Date;

@Component
public class EpochTriggerMonitor {

	@Scheduled(fixedRate = 10000, initialDelay = 5000)
	public void publish() {
		ServerApplication.incrementEpoch();

		Date date = new Date();
		System.out.println("[Server"+ new Timestamp(date.getTime())+ "] Epoch change, current epoch now is " + ServerApplication.epoch);
	}


}
