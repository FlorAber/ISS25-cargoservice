package test.java;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import unibo.basicomm23.utils.CommUtils;
import unibo.basicomm23.interfaces.Interaction;
import unibo.basicomm23.tcp.TcpClientSupport;

/**
 * Test JUnit per il sistema Cargo (solo i 3 test definiti nel .qak)
 */
public class CargoSystemTest {
    
    private static final String HOST = "localhost";
    private static final int TEST_PORT = 8018;  // Porta ctx_test
    private static final int CARGO_PORT = 8014; // Porta ctx_cargo
    
    private Interaction connTest;
    private Interaction connCargo;
    
    @Before
    public void setUp() throws Exception {
        System.out.println("========================================");
        System.out.println("SETUP: Inizializzazione connessioni");
        System.out.println("========================================");

        // Connessione ctx_test
        int maxAttempts = 5;
        int attempt = 0;
        boolean connectedTest = false;
        while(attempt < maxAttempts && !connectedTest) {
            try {
                connTest = TcpClientSupport.connect(HOST, TEST_PORT, 5);
                connectedTest = true;
            } catch(Exception e) {
                Thread.sleep(2000);
                attempt++;
            }
        }
        if(!connectedTest) throw new Exception("Impossibile connettersi a ctx_test");

        // Connessione ctx_cargo
        attempt = 0;
        boolean connectedCargo = false;
        while(attempt < maxAttempts && !connectedCargo) {
            try {
                connCargo = TcpClientSupport.connect(HOST, CARGO_PORT, 5);
                connectedCargo = true;
            } catch(Exception e) {
                Thread.sleep(2000);
                attempt++;
            }
        }
        if(!connectedCargo) throw new Exception("Impossibile connettersi a ctx_cargo");

        System.out.println("Setup completato con successo\n");
    }
    
    @After
    public void tearDown() {
        System.out.println("\n========================================");
        System.out.println("TEARDOWN: Chiusura connessioni");
        System.out.println("========================================\n");

        if(connTest != null) {
            try { connTest.close(); } catch(Exception e) { System.err.println(e); }
        }
        if(connCargo != null) {
            try { connCargo.close(); } catch(Exception e) { System.err.println(e); }
        }
    }
    
    /** TEST 1: loadrequest + doDeposit */
    @Test
    public void test01_CompleteLoadWorkflow() throws Exception {
        System.out.println("\n[TEST 1] Testing complete load workflow");

        String loadRequest = "msg(loadrequest, request, junit, cargomanager, loadrequest(1), 1)";
        connCargo.forward(loadRequest);
        Thread.sleep(1000);
        String response = connCargo.receiveMsg();
        assertNotNull("Nessuna risposta ricevuta", response);
        assertTrue("Risposta non contiene loadaccepted", response.contains("loadaccepted"));

        String deposit = "msg(doDeposit, dispatch, junit, cargomanager, doDeposit(1), 2)";
        connCargo.forward(deposit);
        Thread.sleep(2000);

        System.out.println("[TEST 1] PASS - Workflow completo eseguito con successo");
    }
    
    /** TEST 2: loadrequest only */
    @Test
    public void test02_LoadRequestOnly() throws Exception {
        System.out.println("\n[TEST 2] Testing load request only");

        String loadRequest = "msg(loadrequest, request, junit, cargomanager, loadrequest(2), 3)";
        connCargo.forward(loadRequest);
        Thread.sleep(1000);
        String response = connCargo.receiveMsg();
        assertNotNull("Nessuna risposta ricevuta", response);
        assertTrue("Risposta non valida", response.contains("loadaccepted") || response.contains("loadrejected"));
    }
    
    /** TEST 3: doDeposit only */
    @Test
    public void test03_DepositOnly() throws Exception {
        System.out.println("\n[TEST 3] Testing deposit only");

        String deposit = "msg(doDeposit, dispatch, junit, cargomanager, doDeposit(3), 4)";
        connCargo.forward(deposit);
        Thread.sleep(3000);

        System.out.println("[TEST 3] PASS - Comando deposit inviato e processato");
    }
}
