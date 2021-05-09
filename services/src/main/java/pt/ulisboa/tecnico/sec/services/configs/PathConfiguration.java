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
	public static final String SERVER_ECHO = "/echo";
	public static final String SERVER_READY = "/ready";

	// Key Paths

	public static final String CLIENT_KEY_FOLDER = System.getProperty("user.dir") + "/services/src/main/java/pt/ulisboa/tecnico/sec/services/keys/client";
	public static final String SERVER_KEY_FOLDER = System.getProperty("user.dir") + "/services/src/main/java/pt/ulisboa/tecnico/sec/services/keys/server";

	private PathConfiguration() {}
	
	public static String buildUrl(String host, String endpoint) { return host + endpoint; }
	
	public static String getServerUrl(int server) { return HOST + ":" + (SERVER_PORT_BASE + server); }
	public static String getClientURL(int client) { return buildUrl(HOST + ":" + (9000 + client), "/proof"); }
	public static String getGetReportURL(int server) { return buildUrl(getServerUrl(server), GET_REPORT_ENDPOINT); }
	public static String getSubmitReportURL(int server) { return buildUrl(getServerUrl(server), SUBMIT_REPORT_ENDPOINT); }
	public static String getObtainUsersAtLocationEpochURL(int server) { return buildUrl(getServerUrl(server), OBTAIN_USERS_AT_LOCATION_EPOCH_ENDPOINT); }
	public static String getGetProofsAtEpochsURL(int server) { return buildUrl(getServerUrl(server), GET_PROOFS_AT_EPOCHS_ENDPOINT); }

	public static String getServerPublicKey(String serverId) { return SERVER_KEY_FOLDER + "/S" + serverId + "pub.key"; }
	public static String getServerPrivateKey(String serverId) { return SERVER_KEY_FOLDER + "/S" + serverId + "priv.key"; }

}
