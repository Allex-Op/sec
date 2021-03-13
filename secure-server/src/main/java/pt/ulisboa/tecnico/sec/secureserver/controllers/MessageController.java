package pt.ulisboa.tecnico.sec.secureserver.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import pt.ulisboa.tecnico.sec.services.interfaces.IMessageService;

@RestController
public class MessageController {
	
	private IMessageService messageService;
	
	@Autowired
	public MessageController(IMessageService messageService) {
		this.messageService = messageService;
	}
	
	@GetMapping("/messages")
	public List<String> getAllMessages() {
		return this.messageService.getAllMessages();
	}

	@PostMapping("/messages")
	public String echo(@RequestBody String msg) {
		return this.messageService.echo(msg);
	}
	
}
