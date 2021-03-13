package pt.ulisboa.tecnico.sec.services.configs;

public class PathConfiguration {
	
	public static final String ACCESS_PROTOCOL = "http";
	public static final String HOST = ACCESS_PROTOCOL + "://localhost:8080";
	public static final String MESSAGES_API = HOST + "/messages";
	
	private PathConfiguration() {}

}
