package pt.ulisboa.tecnico.sec.services.configs;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class PathConfiguration {
	
	public static final String ACCESS_PROTOCOL = "http";

	public static final String HOST = ACCESS_PROTOCOL + "://127.0.0.1";

	public static final String SERVER_URL = HOST + ":8080";

	public static final String USER_API = SERVER_URL + "/locations";

	// Key Paths

	public static final String CLIENT_KEY_FOLDER = "/home/thunder-marks/IdeaProjects/Secure-Communication/services/src/main/java/pt/ulisboa/tecnico/sec/services/keys/client";//change
	public static final String SERVER_KEY_FOLDER = "/home/thunder-marks/IdeaProjects/Secure-Communication/services/src/main/java/pt/ulisboa/tecnico/sec/services/keys/server";//change

	public static final String CLIENT_PUBLIC_KEY_NAME = "Cpub.key";
	public static final String CLIENT_PRIVATE_KEY_NAME = "Cpriv.key";
	public static final String SERVER_PUBLIC_KEY_NAME = "Spub.key";
	public static final String SERVER_PRIVATE_KEY_NAME = "Spriv.key";

	public static final String CLIENT_PUBLIC_KEY = CLIENT_KEY_FOLDER + "/" + CLIENT_PUBLIC_KEY_NAME;
	public static final String SERVER_PUBLIC_KEY = SERVER_KEY_FOLDER + "/" + SERVER_PUBLIC_KEY_NAME;
	public static final String CLIENT_PRIVATE_KEY = CLIENT_KEY_FOLDER + "/" + CLIENT_PRIVATE_KEY_NAME;
	public static final String SERVER_PRIVATE_KEY = SERVER_KEY_FOLDER + "/" + SERVER_PRIVATE_KEY_NAME;

	private PathConfiguration() {}
	
	public static String getClientURL(int client) {
		return HOST + ":" + (9000 + client) + "/proof";
	}

}
