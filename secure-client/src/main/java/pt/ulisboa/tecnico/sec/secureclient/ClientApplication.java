package pt.ulisboa.tecnico.sec.secureclient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ClientApplication {
	
	public static String userId;
	public static int epoch = 0;
	
	public static void main(String[] args) {
		if (args.length < 2) {
			System.out.println("Need 2 arguments: <port> <userId_integer>");
			System.exit(0);
			return;
		}
		
		userId = args[1];

		SpringApplication.run(ClientApplication.class, args);
	}
	
	public static void incrementEpoch() {
		epoch++;
	}

}
