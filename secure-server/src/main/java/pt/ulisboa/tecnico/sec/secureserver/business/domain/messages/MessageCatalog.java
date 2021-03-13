package pt.ulisboa.tecnico.sec.secureserver.business.domain.messages;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import pt.ulisboa.tecnico.sec.services.utils.Clock;

@Repository
public class MessageCatalog {

	private static List<Message> messages = new ArrayList<>();

	private MessageCatalog() {}

	public void saveMessage(String msg) {
		Message m = new Message(msg, Clock.getDateTime());
		messages.add(m);
	}

	public List<String> readAllMessages() {
		return messages.stream().map(Message::toString).collect(Collectors.toList());
	}

}
