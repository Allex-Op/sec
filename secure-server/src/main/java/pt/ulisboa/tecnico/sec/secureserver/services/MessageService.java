package pt.ulisboa.tecnico.sec.secureserver.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pt.ulisboa.tecnico.sec.secureserver.business.handlers.EchoMessageHandler;
import pt.ulisboa.tecnico.sec.services.interfaces.IMessageService;

@Service
public class MessageService implements IMessageService {
	
	private EchoMessageHandler echoMessageHandler;
	
	@Autowired
	public MessageService(EchoMessageHandler echoMessageHandler) {
		this.echoMessageHandler = echoMessageHandler;
	}

	public String echo(String msg) {
		return this.echoMessageHandler.echo(msg);
	}
	
	public List<String> getAllMessages() {
		return this.echoMessageHandler.getAllMessages();
	}
	
}
