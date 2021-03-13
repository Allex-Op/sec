package pt.ulisboa.tecnico.sec.secureclient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import pt.ulisboa.tecnico.sec.secureclient.services.MessageService;

@SpringBootApplication
public class ClientApplication {
	
	private MessageService messageService;
	
	@Autowired
	public ClientApplication(MessageService messageService) {
		this.messageService = messageService;
	}
	
	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(ClientApplication.class, args);
		SpringApplication.exit(context, () -> 0);
	}
	
	@Bean
	public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
		return args -> {
			System.out.println("Client will send 2 messages:");
			
			String response1 = messageService.echo("primeira mensagem");
			System.out.println("Received from server: " + response1);
			String response2 = messageService.echo("esta eh a segunda mensagem");
			System.out.println("Received from server: " + response2);
		};
	}

}
