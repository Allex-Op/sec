package pt.ulisboa.tecnico.sec.services.interfaces;

import java.util.List;

public interface IMessageService {

	public String echo(String msg);

	public List<String> getAllMessages();

}
