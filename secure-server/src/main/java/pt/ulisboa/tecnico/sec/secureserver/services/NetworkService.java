package pt.ulisboa.tecnico.sec.secureserver.services;

import org.hibernate.tool.schema.internal.exec.ScriptTargetOutputToFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pt.ulisboa.tecnico.sec.secureserver.ServerApplication;
import pt.ulisboa.tecnico.sec.services.configs.ByzantineConfigurations;
import pt.ulisboa.tecnico.sec.services.configs.PathConfiguration;
import pt.ulisboa.tecnico.sec.services.dto.RequestDTO;
import pt.ulisboa.tecnico.sec.services.dto.SecureDTO;
import pt.ulisboa.tecnico.sec.services.exceptions.ApplicationException;
import pt.ulisboa.tecnico.sec.services.utils.crypto.CryptoService;
import pt.ulisboa.tecnico.sec.services.utils.crypto.CryptoUtils;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class NetworkService {

    private static RestTemplate restTemplate = new RestTemplate();
    private static Boolean newRequest = false;
    private static Map<String, Object> deliverWait = new ConcurrentHashMap<>();
    private static Map<String, Map<String, RequestDTO>> echos = new ConcurrentHashMap<>();
    private static Map<String, Map<String, RequestDTO>> readys = new ConcurrentHashMap<>();
    private static Map<String, Boolean> sentEcho = new ConcurrentHashMap<>();
    private static Map<String, Boolean> sentReady = new ConcurrentHashMap<>();
    private static Map<String, Boolean> delivered = new ConcurrentHashMap<>();
    private static final Object readyWait = new Object();
    private static final Object echoWait = new Object();
    public static boolean initDone = false;

    private static Object lock = new Object();
    private static Thread echoJob;
    private static Thread readyJob;

    public static void init() {
        initDone = true;
        waitForQuorum();
    }

    public static void waitForQuorum() {
        //Thread waiting for quorum of echos
        echoJob = new Thread(() -> {
            while (true) {
                if (newRequest) {
                    //Count echos

                    for (Map.Entry<String, Map<String, RequestDTO>> clientMap : echos.entrySet()) {
                        final Collection<RequestDTO> requests = clientMap.getValue().values();
                        Set<RequestDTO> uniqueSet = new HashSet<>(requests);
                        for (RequestDTO temp : uniqueSet) {
                            if (temp == null) {
                                continue;
                            }
                            //If we have a quorum of echos and sentready = false
                            if (Collections.frequency(requests, temp) > (ByzantineConfigurations.NUMBER_OF_SERVERS + ByzantineConfigurations.MAX_BYZANTINE_FAULTS) / 2
                                    && !sentReady.containsKey(clientMap.getKey())) {
                                sentReady.putIfAbsent(clientMap.getKey(), true);
                                temp.setServerId(ServerApplication.serverId);
                                sendReadys(temp);
                            }
                        }
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        return;
                    }
                }
            }
        });

        //Waiting for quorum of readys
        readyJob = new Thread(() -> {
            while (true) {
                if (newRequest) {
                    //Count Readys
                    for (Map.Entry<String, Map<String, RequestDTO>> clientMap : readys.entrySet()) { //TODO EMPTY
                        final Collection<RequestDTO> requests = clientMap.getValue().values();
                        Set<RequestDTO> uniqueSet = new HashSet<>(requests);
                        for (RequestDTO temp : uniqueSet) {
                            if (temp == null) {
                                continue;
                            }

                            //If we have readys > f and sentready = false
                            if (Collections.frequency(requests, temp) > ByzantineConfigurations.MAX_BYZANTINE_FAULTS
                                    && !sentReady.containsKey(clientMap.getKey())) {
                                sentReady.putIfAbsent(clientMap.getKey(), true);
                                temp.setServerId(ServerApplication.serverId);
                                sendReadys(temp);
                            }

                            //If we have readys > 2f and delivered = false
                            if (Collections.frequency(requests, temp) > 2 * ByzantineConfigurations.MAX_BYZANTINE_FAULTS
                                    && !delivered.containsKey(clientMap.getKey())) {
                                delivered.putIfAbsent(clientMap.getKey(), true);
                                //Trigger deliver
                                synchronized (deliverWait.get(clientMap.getKey())) {
                                    deliverWait.get(clientMap.getKey()).notify();
                                }
                            }
                        }
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        return;
                    }
                }
            }
        });
        echoJob.start();
        readyJob.start();
    }

    static void sendReadys(RequestDTO request) {
        for (int i = 1; i<= ServerApplication.numberOfServers; i++) {
            int serverId = i;
            CompletableFuture.runAsync(() -> {
                // Convert the above request body to a secure request object
                byte[] randomBytes = CryptoUtils.generateRandom32Bytes();
                SecureDTO secureDTO = CryptoService.generateNewSecureDTO(request, ServerApplication.serverId, randomBytes, serverId + "");

                String url = PathConfiguration.buildUrl(PathConfiguration.getServerUrl(serverId), PathConfiguration.SERVER_READY);
                sendMessageToServer(secureDTO, url);
            });
        }
    }

    private static SecureDTO sendMessageToServer(SecureDTO message, String url) {
        // Set HTTP headers
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

        // Send request and return the SecureDTO with the ReportDTO encapsulated
        HttpEntity<SecureDTO> entity = new HttpEntity<>(message, headers);
        ResponseEntity<SecureDTO> result = restTemplate.exchange(url, HttpMethod.POST, entity, SecureDTO.class);
        return result.getBody();
    }

    public static void sendBroadcast(RequestDTO request) throws ApplicationException {
        newRequest = true;
        synchronized (lock) {
            if (!initDone) {    //TODO: Devia estar sincronizado TOCTOU, varias thread podem ler false e tentar inicializar
                init();
            }
        }
        initClient(request.getClientId()); // restart for that client

        deliverWait.putIfAbsent(request.getClientId(), new Object());

        if (!sentEcho.containsKey(request.getClientId())) { //if sentecho = false
            sentEcho.put(request.getClientId(), true);
            for (int i = 1; i<= ServerApplication.numberOfServers; i++) {
                // Convert the above request body to a secure request object
                byte[] randomBytes = CryptoUtils.generateRandom32Bytes();
                SecureDTO secureDTO = CryptoService.generateNewSecureDTO(request, ServerApplication.serverId, randomBytes, i + "");

                String url = PathConfiguration.buildUrl(PathConfiguration.getServerUrl(i), PathConfiguration.SERVER_ECHO);
                sendMessageToServer(secureDTO, url);
            }
        }

        if (!delivered.containsKey(request.getClientId())) {
            synchronized (deliverWait.get(request.getClientId())) {
                try {
                    deliverWait.get(request.getClientId()).wait(20000); //TODO verify wait with few seconds
                } catch (InterruptedException e) {
                    throw new ApplicationException("Error in Broadcast", e);
                }
            }
        }

        cleanClient(request.getClientId());
        newRequest = false;

        if (!delivered.containsKey(request.getClientId())) {
            throw new ApplicationException("Broadcast was unable to met a quorum");
        }

    }

    private static void initClient(String clientId) {
        sentEcho.remove(clientId);
        sentReady.remove(clientId);
        delivered.remove(clientId);
    }

    private static void cleanClient(String clientId) {
        echos.remove(clientId);
        readys.remove(clientId);
    }

    public void echo(RequestDTO request) {
        System.out.println("Im executing the echo method");
        echos.putIfAbsent(request.getClientId(), new ConcurrentHashMap<>());
        System.out.println(echos.get(request.getClientId()));
        echos.get(request.getClientId()).put(request.getServerId(), request);
        System.out.println(echos.get(request.getClientId()));
    }

    public void ready(RequestDTO request) {
        System.out.println("Im executing the ready method");
        readys.putIfAbsent(request.getClientId(), new ConcurrentHashMap<>());
        System.out.println(readys.get(request.getClientId()));
        readys.get(request.getClientId()).put(request.getServerId(), request);
        System.out.println(readys.get(request.getClientId()));
    }

}
