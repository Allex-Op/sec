package pt.ulisboa.tecnico.sec.secureserver.business.domain.messages;

import java.time.LocalDateTime;

public class Message {

	private String textMessage;
	private LocalDateTime dateTime;

	public Message(String textMessage, LocalDateTime dateTime) {
		this.textMessage = textMessage;
		this.dateTime = dateTime;
	}

	/**
	 * @return the text message
	 */
	public String getTextMessage() {
		return textMessage;
	}

	/**
	 * @return the dateTime
	 */
	public LocalDateTime getDateTime() {
		return dateTime;
	}

	@Override
	public String toString() {
		return "'" + textMessage + "' at " + dateTime.toString();
	}

}
