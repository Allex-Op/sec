package pt.ulisboa.tecnico.sec.secureclient.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {
	
	@GetMapping("/")
	public String obtainLocationReport() {
		return "Hello World!";
	}

}
