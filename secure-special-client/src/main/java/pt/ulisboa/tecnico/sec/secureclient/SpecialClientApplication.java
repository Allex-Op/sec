package pt.ulisboa.tecnico.sec.secureclient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class SpecialClientApplication extends SpringBootServletInitializer {
	
	public static String userId;
	
	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(SpecialClientApplication.class, args);
		
		if (args.length < 2) {
			System.out.println("Need 2 arguments: <port> <userId_integer>");
			SpringApplication.exit(context, () -> 0);
			return;
		}
		
		userId = args[1];
	}

}
