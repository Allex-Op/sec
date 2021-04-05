package pt.ulisboa.tecnico.sec.services.configs;

public class PathConfiguration {
	
	public static final String ACCESS_PROTOCOL = "http";

	public static final String HOST = ACCESS_PROTOCOL + "://127.0.0.1";

	public static final String SERVER_URL = HOST + ":8080";

	public static final String GET_REPORT_URL = SERVER_URL + "/getReport";
	public static final String SUBMIT_REPORT_URL = SERVER_URL + "/submitReport";


	// Key Paths

	public static final String CLIENT_KEY_FOLDER = System.getProperty("user.dir") + "/services/src/main/java/pt/ulisboa/tecnico/sec/services/keys/client";
	public static final String SERVER_KEY_FOLDER = System.getProperty("user.dir") + "/services/src/main/java/pt/ulisboa/tecnico/sec/services/keys/server";

	public static final String SERVER_PUBLIC_KEY_NAME = "Spub.key";
	public static final String SERVER_PRIVATE_KEY_NAME = "Spriv.key";

	public static final String SERVER_PUBLIC_KEY = SERVER_KEY_FOLDER + "/" + SERVER_PUBLIC_KEY_NAME;
	public static final String SERVER_PRIVATE_KEY = SERVER_KEY_FOLDER + "/" + SERVER_PRIVATE_KEY_NAME;

	public static final String CLIENT1_PRIVATE_KEY = CLIENT_KEY_FOLDER + "/C1priv.key";
	public static final String CLIENT1_PUBLIC_KEY = CLIENT_KEY_FOLDER + "/C1pub.key";
	public static final String CLIENT2_PRIVATE_KEY = CLIENT_KEY_FOLDER + "/C2priv.key";
	public static final String CLIENT2_PUBLIC_KEY = CLIENT_KEY_FOLDER + "/C2pub.key";
	public static final String CLIENT3_PRIVATE_KEY = CLIENT_KEY_FOLDER + "/C3priv.key";
	public static final String CLIENT3_PUBLIC_KEY = CLIENT_KEY_FOLDER + "/C3pub.key";
	public static final String CLIENT4_PRIVATE_KEY = CLIENT_KEY_FOLDER + "/C4priv.key";
	public static final String CLIENT4_PUBLIC_KEY = CLIENT_KEY_FOLDER + "/C4pub.key";

	private PathConfiguration() {}
	
	public static String getClientURL(int client) {
		return HOST + ":" + (9000 + client) + "/proof";
	}

}
