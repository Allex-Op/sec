package pt.ulisboa.tecnico.sec.services.configs;

public class PathConfiguration {
	
	public static final String ACCESS_PROTOCOL = "http";

	public static final String HOST = ACCESS_PROTOCOL + "://127.0.0.1";

	public static final String SERVER_URL = HOST + ":8080";
	public static final int SERVER_PORT_BASE = 9200;

	public static final String GET_REPORT_ENDPOINT =  "/getReport";
	public static final String SUBMIT_REPORT_ENDPOINT =  "/submitReport";
	public static final String OBTAIN_USERS_AT_LOCATION_EPOCH_ENDPOINT = "/locations/management/";
	public static final String GET_PROOFS_AT_EPOCHS_ENDPOINT = "/getProofs";


	// Key Paths

	public static final String CLIENT_KEY_FOLDER = System.getProperty("user.dir") + "/services/src/main/java/pt/ulisboa/tecnico/sec/services/keys/client";
	public static final String SERVER_KEY_FOLDER = System.getProperty("user.dir") + "/services/src/main/java/pt/ulisboa/tecnico/sec/services/keys/server";

	//public static final String SERVER_PUBLIC_KEY_NAME = "S1pub.key";
	//public static final String SERVER_PRIVATE_KEY_NAME = "S1priv.key";

	//public static final String SERVER_PUBLIC_KEY = SERVER_KEY_FOLDER + "/" + SERVER_PUBLIC_KEY_NAME;
	//public static final String SERVER_PRIVATE_KEY = SERVER_KEY_FOLDER + "/" + SERVER_PRIVATE_KEY_NAME;

	private PathConfiguration() {}
	
	public static String getClientURL(int client) {
		return HOST + ":" + (9000 + client) + "/proof";
	}
	public static String getServerUrl(int server) { return HOST + ":" + (SERVER_PORT_BASE + server); }
	public static String getGetReportURL(int server) { return getServerUrl(server) + GET_REPORT_ENDPOINT; }
	public static String getSubmitReportURL(int server) { return getServerUrl(server) + SUBMIT_REPORT_ENDPOINT; }
	public static String getObtainUsersAtLocationEpochURL(int server) { return getServerUrl(server) + OBTAIN_USERS_AT_LOCATION_EPOCH_ENDPOINT; }
	public static String getGetProofsAtEpochsURL(int server) { return getServerUrl(server) + GET_PROOFS_AT_EPOCHS_ENDPOINT; }

	public static String getServerPublicKey(String serverId) { return SERVER_KEY_FOLDER + "/S" + serverId + "pub.key"; }
	public static String getServerPrivateKey(String serverId) { return SERVER_KEY_FOLDER + "/S" + serverId + "priv.key"; }

}
