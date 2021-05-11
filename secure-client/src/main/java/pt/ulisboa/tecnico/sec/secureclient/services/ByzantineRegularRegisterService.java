package pt.ulisboa.tecnico.sec.secureclient.services;

import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import pt.ulisboa.tecnico.sec.secureclient.ClientApplication;
import pt.ulisboa.tecnico.sec.services.configs.ByzantineConfigurations;
import pt.ulisboa.tecnico.sec.services.configs.PathConfiguration;
import pt.ulisboa.tecnico.sec.services.dto.ResponseUserProofsDTO;
import pt.ulisboa.tecnico.sec.services.dto.SecureDTO;
import pt.ulisboa.tecnico.sec.services.exceptions.ApplicationException;
import pt.ulisboa.tecnico.sec.services.exceptions.UnreachableClientException;
import pt.ulisboa.tecnico.sec.services.utils.crypto.CryptoService;
import pt.ulisboa.tecnico.sec.services.utils.crypto.CryptoUtils;

import javax.crypto.SecretKey;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Service used by the operations: obtainUsersAtLocation & requestMyProofs
 */
public class ByzantineRegularRegisterService {
    private static RestTemplate restTemplate = new RestTemplate();

    // Ackowledgments sent by server during step 4
    private static Map<String, SecureDTO> acklist = new ConcurrentHashMap<>();

    // Request ID for the current read, the server must return the same r = rid
    private static AtomicInteger rid = new AtomicInteger(0);

    // Timestamp of the request, used by both server and client
    private static String timestamp = "";

    /**
     * It will send a message "v" with a signature "o" and timestamp "wts"
     * to all servers.
     */
    public void writeToRegisters() {
    }

    /**
     * Called when the timestamp of the request sent
     * to the servers is lower than the timestamp that the
     * servers registers had.
     *
     * It will return without writing if it received more
     * than (N+f)/2 ACKs.
     */
    public void receiveAck() {
    }

    /**
     * Called when the client wants to read
     * something from the server.
     *
     * It will send a request with the same RID
     * to all servers.
     *
     * Then it has to wait for the answer and verify the digital signature
     * if its valid then add to the readlist until there are > (N+f)/2 messages.
     * Pick the value with the highest timestamp and return that to the user.
     */
    public static <P, R> R readFromRegisters(P unsecureDTO, Class<R> responseClass, String userIdSender, String endpoint) throws ApplicationException {
        // Used to block the requesting thread untill all assynchronous requests complete
        CountDownLatch latch = new CountDownLatch(ByzantineConfigurations.NUMBER_OF_SERVERS);

        // Messages received from servers
        ConcurrentHashMap<byte[], SecureDTO> readlist = new ConcurrentHashMap<>();

        SecureDTO response = null;

        // Send to each server a read request with the current RID
        for (int i = 1; i <= ClientApplication.numberOfServers; i++) {
            int serverId = i;
            CompletableFuture.runAsync(() -> {
                try {
                    byte[] randomBytes = CryptoUtils.generateRandom32Bytes();
                    SecureDTO secureDTO = CryptoService.generateNewSecureDTO(unsecureDTO, userIdSender, randomBytes, serverId + "");
                    secureDTO.setRid(rid.incrementAndGet());
                    CryptoService.signSecureDTO(secureDTO, CryptoUtils.getClientPrivateKey(ClientApplication.userId));

                    String url = PathConfiguration.buildUrl(PathConfiguration.getServerUrl(serverId), endpoint);

                    try {
                        SecureDTO sec = sendMessageToServer(secureDTO, url);

                        if (sec == null) {
                            System.out.println("[Client " + ClientApplication.userId + "] Wasn't able to contact server " + serverId);
                        } else {
                            if (secureDTO != null && CryptoService.checkSecureDTODigitalSignature(sec, CryptoUtils.getServerPublicKey(serverId + ""))) {
                                System.out.println("[Client " + ClientApplication.userId + "] Byzantine regular register received secureDTO");
                                readlist.put(randomBytes, sec);
                            }
                        }
                    } catch(Exception e) {
                        throw new UnreachableClientException("[Client "+ ClientApplication.userId+"] Wasn't able to contact server.");
                    }
                } catch (Exception e){
                    System.out.println(e.getMessage());
                }
                latch.countDown();
            });
        }

        // Block requesting thread until all HTTP requests are answered
        boolean responses;
        do {
            try {
                responses = latch.await(2, TimeUnit.SECONDS);
            } catch (Exception e) {
                responses = true;
            }
        } while (!responses);


        // If the number of replies is bigger than (N+f)/2, the byzantine quorum is met and 1 reply is correct
        if(readlist.size() > (ClientApplication.numberOfServers + ByzantineConfigurations.MAX_BYZANTINE_FAULTS) / 2) {
            System.out.println("[Client " + ClientApplication.userId + "] Byzantine regular register obtained minumum quorum.");

            // From the replies, choose the one with highest timestamp and return it
            byte[] randomBytes = highestval(readlist);
            R unwrappedDTO = (R) CryptoService.extractEncryptedData(readlist.get(randomBytes), responseClass, CryptoUtils.createSharedKeyFromString(randomBytes));
            return unwrappedDTO;
        }

        // A byzantine quorum minimum wasn't met.
        throw new ApplicationException("Client " + ClientApplication.userId + " wasn't able to obtain at least (N+f)/2 responses.");
    }

    /**
     *  Sends HTTP request
     */
    private static SecureDTO sendMessageToServer(SecureDTO message, String url) {
        // Set HTTP headers
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

        // Send request and return the SecureDTO with the ReportDTO encapsulated
        HttpEntity<SecureDTO> entity = new HttpEntity<>(message, headers);
        ResponseEntity<SecureDTO> result = restTemplate.exchange(url, HttpMethod.POST, entity, SecureDTO.class);
        return result.getBody();
    }

    /**
     * From a list of secure dto's, returns the one
     * with highest timestamp.
     */
    private static byte[] highestval(ConcurrentHashMap<byte[], SecureDTO> readlist) {
        byte[] highestKey = null;
        long highestTimestamp = 0;

        for (byte[] bytes : readlist.keySet()) {
            if(highestKey == null)
                highestKey = bytes;

            long currTimestamp = readlist.get(bytes).getTimestamp();
            if( currTimestamp > highestTimestamp) {
                highestKey = bytes;
                highestTimestamp = currTimestamp;
            }
        }

        return highestKey;
    }

}
