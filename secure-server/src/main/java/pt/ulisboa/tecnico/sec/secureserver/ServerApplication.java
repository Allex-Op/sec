package pt.ulisboa.tecnico.sec.secureserver;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ServerApplication {

	public static int epoch = 0;
	public static String serverId;

	public static void main(String[] args) {
		if (args.length < 2) {
			System.out.println("Need 2 arguments: <port> <userId_integer>");
			System.exit(0);
			return;
		}
		
		serverId = args[1];
		
		SpringApplication.run(ServerApplication.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
		return args -> {

		};
	}

	public static void incrementEpoch() {
		epoch++;
	}
}


