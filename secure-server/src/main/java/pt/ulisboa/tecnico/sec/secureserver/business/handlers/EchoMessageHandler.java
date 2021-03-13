package pt.ulisboa.tecnico.sec.secureserver.business.handlers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pt.ulisboa.tecnico.sec.secureserver.business.domain.messages.MessageCatalog;

@Service
public class EchoMessageHandler {
	
	private MessageCatalog messageCatalog;
	
	@Autowired
	public EchoMessageHandler(MessageCatalog messageCatalog) {
		this.messageCatalog = messageCatalog;
	}

	public String echo(String msg) {
		this.messageCatalog.saveMessage(msg);
		return msg;
	}

	public List<String> getAllMessages() {
		return this.messageCatalog.readAllMessages();
	}
	
	

}
