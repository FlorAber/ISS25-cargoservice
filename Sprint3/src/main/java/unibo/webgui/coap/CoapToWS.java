package unibo.webgui.coap;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapObserveRelation;
import org.eclipse.californium.core.CoapResponse;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import unibo.basicomm23.utils.CommUtils;
import unibo.webgui.utils.HoldResponseParser;
import unibo.webgui.ws.WSHandler;

@Component
public class CoapToWS {
	// nome che viene risolto da dentro il container
//	private static final String COAP_ENDPOINT = "coap://arch3:8000/ctx_cargoservice/hold";
    private static final String COAP_ENDPOINT_HOLD = "coap://127.0.0.1:8014/ctx_cargo/holdmanager";
    private static final String COAP_ENDPOINT_ROBOT = "coap://127.0.0.1:8014/ctx_cargo/cargorobot";

    private CoapClient clienthold;
    private CoapClient clientrobot;
    private CoapObserveRelation observeRelationHold;
    private CoapObserveRelation observeRelationRobot;

    @Autowired
    private WSHandler wsHandler;
    
    @PostConstruct
    public void init() {
        // inizializza clienthold e clientrobot
        clienthold = new CoapClient(COAP_ENDPOINT_HOLD);
        observeRelationHold = clienthold.observe(new CoapHandler() {
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
        
        clientrobot = new CoapClient(COAP_ENDPOINT_ROBOT);
        observeRelationRobot = clientrobot.observe(new CoapHandler() {
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