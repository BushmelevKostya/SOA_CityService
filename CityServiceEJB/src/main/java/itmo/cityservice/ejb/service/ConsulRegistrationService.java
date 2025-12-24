package itmo.cityservice.ejb.service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.ejb.Schedule;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Singleton
@Startup
public class ConsulRegistrationService {
    
    private static final String CONSUL_HOST = System.getProperty("consul.host", "localhost");
    private static final String CONSUL_PORT = System.getProperty("consul.port", "8500");
    private static final String SERVICE_NAME = "city-service";
    private static final String SERVICE_ID = "city-service-" + System.currentTimeMillis();
    
    private String consulUrl;
    private String serviceAddress;
    private int servicePort;
    private boolean isRegistered = false;
    
    @PostConstruct
    public void init() {
        try {
            consulUrl = "http://" + CONSUL_HOST + ":" + CONSUL_PORT + "/v1";
            serviceAddress = getLocalIpAddress();
            servicePort = getServerPort();
            
            System.out.println("Consul Registration Service initialized");
            System.out.println("Consul URL: " + consulUrl);
            System.out.println("Service Address: " + serviceAddress + ":" + servicePort);

            registerService();
            
        } catch (Exception e) {
            System.err.println("Failed to initialize Consul registration: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private String getLocalIpAddress() throws UnknownHostException {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            System.err.println("Failed to get local IP, using localhost");
            return "127.0.0.1";
        }
    }
    
    private int getServerPort() {
        String port = System.getProperty("jboss.http.port");
        if (port != null) {
            try {
                return Integer.parseInt(port);
            } catch (NumberFormatException e) {
            }
        }
        return 8443;
    }
    
    private void registerService() {
        try {
            Map<String, Object> registration = new HashMap<>();
            registration.put("ID", SERVICE_ID);
            registration.put("Name", SERVICE_NAME);
            registration.put("Address", serviceAddress);
            registration.put("Port", servicePort);
            registration.put("Tags", Collections.singletonList("city-service"));

            Map<String, Object> check = new HashMap<>();
            check.put("ID", "health-check-" + SERVICE_ID);
            check.put("Name", "Health check for " + SERVICE_NAME);
            check.put("HTTP", "http://" + serviceAddress + ":" + servicePort + "/api/v1/health");
            check.put("Interval", "10s");
            check.put("Timeout", "1s");
            check.put("DeregisterCriticalServiceAfter", "1m");
            
            registration.put("Check", check);

            String json = toJson(registration);
            String response = sendPostRequest(consulUrl + "/agent/service/register", json);
            
            if (response != null) {
                isRegistered = true;
                System.out.println("Service successfully registered in Consul: " + SERVICE_ID);
            } else {
                System.err.println("Failed to register service in Consul");
            }
            
        } catch (Exception e) {
            System.err.println("Failed to register service in Consul: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @PreDestroy
    public void destroy() {
        try {
            if (isRegistered) {
                String response = sendGetRequest(consulUrl + "/agent/service/deregister/" + SERVICE_ID);
                if (response != null) {
                    System.out.println("Service deregistered from Consul: " + SERVICE_ID);
                }
                isRegistered = false;
            }
        } catch (Exception e) {
            System.err.println("Failed to deregister service from Consul: " + e.getMessage());
        }
    }

    @Schedule(second = "0,30", minute = "*", hour = "*")
    public void heartbeat() {
        try {
            if (isRegistered) {
                String response = sendGetRequest(consulUrl + "/agent/check/pass/health-check-" + SERVICE_ID);
                if (response != null) {
                    System.out.println("Consul heartbeat sent for service: " + SERVICE_ID);
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to send heartbeat to Consul: " + e.getMessage());
            // Пытаемся перерегистрировать
            try {
                registerService();
            } catch (Exception re) {
                System.err.println("Failed to re-register service: " + re.getMessage());
            }
        }
    }

    private String sendPostRequest(String url, String json) {
        try {
            java.net.URL obj = new java.net.URL(url);
            java.net.HttpURLConnection con = (java.net.HttpURLConnection) obj.openConnection();
            con.setRequestMethod("PUT");
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);
            
            java.io.OutputStream os = con.getOutputStream();
            os.write(json.getBytes());
            os.flush();
            os.close();
            
            int responseCode = con.getResponseCode();
            if (responseCode == 200) {
                return "OK";
            } else {
                System.err.println("POST request failed. Response Code: " + responseCode);
                return null;
            }
        } catch (Exception e) {
            System.err.println("Failed to send POST request: " + e.getMessage());
            return null;
        }
    }
    
    private String sendGetRequest(String url) {
        try {
            java.net.URL obj = new java.net.URL(url);
            java.net.HttpURLConnection con = (java.net.HttpURLConnection) obj.openConnection();
            con.setRequestMethod("PUT");
            
            int responseCode = con.getResponseCode();
            if (responseCode == 200) {
                return "OK";
            } else {
                System.err.println("GET/PUT request failed. Response Code: " + responseCode);
                return null;
            }
        } catch (Exception e) {
            System.err.println("Failed to send GET/PUT request: " + e.getMessage());
            return null;
        }
    }
    
    private String toJson(Map<String, Object> map) {
        StringBuilder json = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (!first) json.append(",");
            json.append("\"").append(entry.getKey()).append("\":");
            if (entry.getValue() instanceof String) {
                json.append("\"").append(entry.getValue()).append("\"");
            } else if (entry.getValue() instanceof Map) {
                json.append(toJson((Map<String, Object>) entry.getValue()));
            } else if (entry.getValue() instanceof java.util.List) {
                json.append("[\"");
                java.util.List<String> list = (java.util.List<String>) entry.getValue();
                for (int i = 0; i < list.size(); i++) {
                    if (i > 0) json.append("\",\"");
                    json.append(list.get(i));
                }
                json.append("\"]");
            } else {
                json.append(entry.getValue());
            }
            first = false;
        }
        json.append("}");
        return json.toString();
    }
}
