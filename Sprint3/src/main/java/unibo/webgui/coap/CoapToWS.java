package unibo.webgui.coap;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapObserveRelation;
import org.eclipse.californium.core.CoapResponse;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import unibo.basicomm23.msg.ProtocolType;
import unibo.basicomm23.utils.CommUtils;
import unibo.basicomm23.utils.ConnectionFactory;
import unibo.webgui.utils.HoldResponseParser;
import unibo.webgui.ws.WSHandler;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;


@Component
public class CoapToWS {
	//Local development
//    private static final String COAP_ENDPOINT_HOLD = "coap://127.0.0.1:8014/ctx_cargo/holdmanager";
//    private static final String COAP_ENDPOINT_ROBOT = "coap://127.0.0.1:8014/ctx_cargo/cargorobot";

	//Docker images
//    private static final String COAP_ENDPOINT_HOLD = "coap://sprint1_core:8014/ctx_cargo/holdmanager";
//    private static final String COAP_ENDPOINT_ROBOT = "coap://sprint1_core:8014/ctx_cargo/cargorobot";
//	
//    private CoapClient clienthold;
//    private CoapClient clientrobot;
//    private CoapObserveRelation observeRelationHold;
//    private CoapObserveRelation observeRelationRobot;
//
//    @Autowired
//    private WSHandler wsHandler;
//       
//    @PostConstruct
//    public void init() {	
//    	
//    	String coapHost;
//    	
//        // Risolvi l'hostname in IP
//        try {
//            java.net.InetAddress address = java.net.InetAddress.getByName("sprint1_core");
//            coapHost = address.getHostAddress();
//            System.out.println("Hostname sprint1_core risolto in: " + coapHost);
//        } catch (Exception e) {
//            System.err.println("Errore risoluzione hostname sprint1_core: " + e.getMessage());
//            // Fallback all'hostname originale o a localhost
//            coapHost = "127.0.0.1"; // o "127.0.0.1" se necessario
//        }
//        
//        clienthold = new CoapClient("coap", coapHost, 8014, "ctx_cargo","holdmanager");
//        observeRelationHold = clienthold.observe(
//        	new CoapHandler() {
//	            @Override
//	            public void onLoad(CoapResponse response) {
//	                String content = response.getResponseText();
//	                CommUtils.outblue("CoAP payload - hold: " + content);
//	                if(content != null) {
//	                	try {
//		                    JSONObject payload = HoldResponseParser.parseHoldState(content);
//		                    if (payload != null) {
//		                        wsHandler.sendToAll(payload.toString());
//		                    } else {
//		                        CommUtils.outred("Evento CoAP non valido: " + content);
//		                    }
//		                } catch (Exception e) {
//		                    e.printStackTrace();
//		                }
//	                }
//	            }
//	
//	            @Override
//	            public void onError() {
//	                System.err.println("Errore nell'osservazione CoAP su " + COAP_ENDPOINT_HOLD);
//	            }
//        });
//        System.out.println("Iniziata osservazione CoAP su: " + COAP_ENDPOINT_HOLD);
//        
//        clientrobot = new CoapClient("coap", coapHost, 8014, "ctx_cargo","cargorobot");
//        observeRelationRobot = clientrobot.observe(
//        	new CoapHandler() {
//            @Override
//            public void onLoad(CoapResponse response) {
//                String content = response.getResponseText();
//                CommUtils.outblue("CoAP payload - robot: " + content);
//
//                if(content != null) {
//	                try {
//	                    JSONObject payload = HoldResponseParser.parseRobotState(content);
//	                    if (payload != null) {
//	                        wsHandler.sendToAll(payload.toString());
//	                    } else {
//	                        CommUtils.outred("Evento CoAP non valido: " + content);
//	                    }
//	                } catch (Exception e) {
//	                    e.printStackTrace();
//	                }
//                }
//            }
//
//            @Override
//            public void onError() {
//                System.err.println("Errore nell'osservazione CoAP su " + COAP_ENDPOINT_ROBOT);
//            }
//        });
//        System.out.println("Iniziata osservazione CoAP su: " + COAP_ENDPOINT_ROBOT);
//    }
		//Local development
//		private static final String HOSTNAME = "localhost";
		//Docker
		private static final String HOSTNAME = "mosquitto";
	    private static final String TOPIC = "coreevents";
	
	    private MqttClient client;
	
	    @Autowired
	    private WSHandler wsHandler;
	
	    @PostConstruct
	    public void init() {
	    	
	    	String host;
	    	
	        try {
	            java.net.InetAddress address = java.net.InetAddress.getByName(HOSTNAME);
	            host = address.getHostAddress();
	            System.out.println("Hostname " + HOSTNAME + " risolto in: " + host);
	        } catch (Exception e) {
	            System.err.println("Errore risoluzione hostname " + HOSTNAME + " : " + e.getMessage());
	            // Fallback all'hostname originale o a localhost
	            host = "sprint1_core"; // o "127.0.0.1" se necessario
	        }
	    	
	        String BROKER_URI = "tcp://" + host + ":1883";
	        
	        try {
	            client = new MqttClient(BROKER_URI, MqttClient.generateClientId());
	            client.connect();
	
	            // Subscribe ai topic
	            client.subscribe(TOPIC, (topic, msg) -> {
	                String payload = new String(msg.getPayload());
	                
	                // Controlla se è robotstate
	                if (payload.contains("robotstate(")) {
	                    // Estrae il contenuto tra parentesi
	                    int start = payload.indexOf("robotstate(") + "robotstate(".length();
	                    int end = payload.indexOf(")", start);
	                    String content = payload.substring(start, end);
	                    System.out.println("robotstate content: " + content);
	                    
	                    if(content != null) {
		                	try {
			                    JSONObject jsonpayload = HoldResponseParser.parseRobotState(content);
			                    if (jsonpayload != null) {
			                        wsHandler.sendToAll(jsonpayload.toString());
			                    } else {
			                        CommUtils.outred("Payload non valido: " + content);
			                    }
			                } catch (Exception e) {
			                    e.printStackTrace();
			                }
		                }
	                    
	                }

	                // Controlla se è holdupdated
	                else if (payload.contains("holdupdated('")) {
	                    // Estrae il contenuto JSON tra apici
	                    int start = payload.indexOf("holdupdated('") + "holdupdated('".length();
	                    int end = payload.indexOf("')", start);
	                    String content = payload.substring(start, end);
	                    System.out.println("holdupdated content: " + content);

	                    if(content != null) {
		                	try {
			                    JSONObject jsonpayload = HoldResponseParser.parseHoldState(content);
			                    if (jsonpayload != null) {
			                        wsHandler.sendToAll(jsonpayload.toString());
			                    } else {
			                        CommUtils.outred("Payload non valido: " + content);
			                    }
			                } catch (Exception e) {
			                    e.printStackTrace();
			                }
		                }
	                    
	                }
	                
	            });
	
	        } catch (MqttException e) {
	            e.printStackTrace();
	            System.err.println("Errore connessione al broker hostname " + HOSTNAME + " : " + e.getMessage());
	        }
	    }
	
}