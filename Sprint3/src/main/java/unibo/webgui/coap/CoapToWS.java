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

import java.net.URI;
import java.net.URISyntaxException;

@Component
public class CoapToWS {
	//Local development
//    private static final String COAP_ENDPOINT_HOLD = "coap://127.0.0.1:8014/ctx_cargo/holdmanager";
//    private static final String COAP_ENDPOINT_ROBOT = "coap://127.0.0.1:8014/ctx_cargo/cargorobot";

	//Docker images
    private static final String COAP_ENDPOINT_HOLD = "coap://sprint1_core:8014/ctx_cargo/holdmanager";
    private static final String COAP_ENDPOINT_ROBOT = "coap://sprint1_core:8014/ctx_cargo/cargorobot";
	
    private CoapClient clienthold;
    private CoapClient clientrobot;
    private CoapObserveRelation observeRelationHold;
    private CoapObserveRelation observeRelationRobot;

    @Autowired
    private WSHandler wsHandler;
       
    @PostConstruct
    public void init() {	
    	
    	String coapHost;
    	
        // Risolvi l'hostname in IP
        try {
            java.net.InetAddress address = java.net.InetAddress.getByName("sprint1_core");
            coapHost = address.getHostAddress();
            System.out.println("Hostname sprint1_core risolto in: " + coapHost);
        } catch (Exception e) {
            System.err.println("Errore risoluzione hostname sprint1_core: " + e.getMessage());
            // Fallback all'hostname originale o a localhost
            coapHost = "sprint1_core"; // o "127.0.0.1" se necessario
        }
        
        clienthold = new CoapClient("coap", coapHost, 8014, "ctx_cargo","holdmanager");
        observeRelationHold = clienthold.observe(
        		new CoapHandler() {
            @Override
            public void onLoad(CoapResponse response) {
                String content = response.getResponseText();
                CommUtils.outblue("CoAP payload - hold: " + content);

                try {
                    JSONObject payload = HoldResponseParser.parseHoldState(content);
                    if (payload != null) {
                        wsHandler.sendToAll(payload.toString());
                    } else {
                        CommUtils.outred("Evento CoAP non valido: " + content);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError() {
                System.err.println("Errore nell'osservazione CoAP su " + COAP_ENDPOINT_HOLD);
            }
        });
        System.out.println("Iniziata osservazione CoAP su: " + COAP_ENDPOINT_HOLD);
        
        clientrobot = new CoapClient("coap", coapHost, 8014, "ctx_cargo","cargorobot");
        observeRelationRobot = clientrobot.observe(
        	new CoapHandler() {
            @Override
            public void onLoad(CoapResponse response) {
                String content = response.getResponseText();
                CommUtils.outblue("CoAP payload - robot: " + content);

                try {
                    JSONObject payload = HoldResponseParser.parseRobotState(content);
                    if (payload != null) {
                        wsHandler.sendToAll(payload.toString());
                    } else {
                        CommUtils.outred("Evento CoAP non valido: " + content);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError() {
                System.err.println("Errore nell'osservazione CoAP su " + COAP_ENDPOINT_ROBOT);
            }
        });
        System.out.println("Iniziata osservazione CoAP su: " + COAP_ENDPOINT_ROBOT);
    }
}