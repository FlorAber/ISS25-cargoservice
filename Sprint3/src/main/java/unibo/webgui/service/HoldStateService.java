package unibo.webgui.service;

import org.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import unibo.basicomm23.interfaces.IApplMessage;
import unibo.basicomm23.interfaces.Interaction;
import unibo.basicomm23.msg.ProtocolType;
import unibo.basicomm23.utils.CommUtils;
import unibo.basicomm23.utils.ConnectionFactory;
import unibo.webgui.utils.HoldResponseParser;
import unibo.webgui.ws.WSHandler;

@RestController
public class HoldStateService {

    @Autowired
    private WSHandler wsHandler;

    private Interaction conn;

    public HoldStateService() {
        try {
        	//Local development
            conn = ConnectionFactory.createClientSupport23(ProtocolType.tcp, "127.0.0.1", "8014");
            //Docker image
//        	conn = ConnectionFactory.createClientSupport23(ProtocolType.tcp, "sprint1_core", "8014");
        } catch (Exception e) {
            System.err.println("Errore nella connessione TCP iniziale: " + e.getMessage());
        }
    }

    @GetMapping("/holdstate")
    public String getHoldState() {
        try {
            IApplMessage request = CommUtils.buildRequest("webgui", "getholdstate", "getholdstate(X)", "holdmanager");
            IApplMessage response = conn.request(request);
            CommUtils.outblue("hold-state query response:" + response.msgContent());
            
            String jsonString = response.msgContent().substring(
                    "'holdstate(".length(), 
                    response.msgContent().length() - 2
                );
            
            JSONObject payload = HoldResponseParser.parseHoldState(jsonString);
            if (payload != null) {
                wsHandler.sendToAll(payload.toString());
                return payload.toString();
            } else {
                return "{\"error\":\"payload nullo\"}";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"error\":\"" + e.getMessage() + "\"}";
        }
    }
    
    @GetMapping("/sonardetection")
    public void sonardetection() {
    	CommUtils.outblue("sent detection");
        try {
            IApplMessage request = CommUtils.buildEvent("webgui", "doDeposit", "X");
            conn.forward(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @GetMapping("/sonarerror")
    public void sonarerror() {
        try {
            IApplMessage request = CommUtils.buildEvent("webgui", "sonaralert", "X");
            conn.forward(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @GetMapping("/sonarok")
    public void sonarok() {
        try {
            IApplMessage request = CommUtils.buildEvent("webgui", "sonarok", "X");
            conn.forward(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
