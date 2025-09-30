package test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import unibo.basicomm23.interfaces.IApplMessage;
import unibo.basicomm23.msg.ProtocolType;
import unibo.basicomm23.utils.CommUtils;
import unibo.basicomm23.utils.ConnectionFactory;
import unibo.basicomm23.interfaces.Interaction;

import static org.junit.Assert.*;

public class testInteractions {
    
    private Interaction connCargoManager;
    private static final String HOST = "localhost";
    private static final int PORT_CARGO = 8014;
    private static final int TIMEOUT = 5000;
    
    @Before
    public void setUp() throws Exception {
        // Connessione al CargoManager
        connCargoManager = ConnectionFactory.createClientSupport(
            ProtocolType.tcp, 
            HOST, 
            String.valueOf(PORT_CARGO)
        );
        
        CommUtils.outblue("testInteractions | Setup completed - Connected to CargoManager");
        Thread.sleep(1000); // Attende che il sistema sia pronto
    }
    
    @After
    public void tearDown() throws Exception {
        if (connCargoManager != null) {
            connCargoManager.close();
        }
        CommUtils.outblue("testInteractions | TearDown completed");
    }
    
    /**
     * TEST 1: Verifica che una loadrequest venga accettata correttamente
     */
    @Test
    public void testLoadRequestAccepted() throws Exception {
        CommUtils.outcyan("========================================");
        CommUtils.outcyan("TEST 1: Load Request Accepted");
        CommUtils.outcyan("========================================");
        
        // Invia loadrequest
        String loadRequestMsg = CommUtils.buildRequest(
            "tester", 
            "loadrequest", 
            "loadrequest(1)", 
            "cargomanager"
        ).toString();
        
        CommUtils.outblue("Sending: " + loadRequestMsg);
        String reply = connCargoManager.request(loadRequestMsg);
        CommUtils.outgreen("Received: " + reply);
        
        // Verifica che la risposta sia loadaccepted
        assertNotNull("Reply should not be null", reply);
        assertTrue("Reply should contain loadaccepted", 
            reply.contains("loadaccepted"));
        
        CommUtils.outgreen("[TEST 1] PASSED ✓");
    }
    
    /**
     * TEST 2: Verifica workflow completo loadrequest + doDeposit
     */
    @Test
    public void testCompleteLoadWorkflow() throws Exception {
        CommUtils.outcyan("========================================");
        CommUtils.outcyan("TEST 2: Complete Load Workflow");
        CommUtils.outcyan("========================================");
        
        // STEP 1: Invia loadrequest
        String loadRequestMsg = CommUtils.buildRequest(
            "tester", 
            "loadrequest", 
            "loadrequest(2)", 
            "cargomanager"
        ).toString();
        
        CommUtils.outblue("Step 1 - Sending loadrequest: " + loadRequestMsg);
        String reply = connCargoManager.request(loadRequestMsg);
        CommUtils.outgreen("Received: " + reply);
        
        // Verifica loadaccepted
        assertTrue("Load should be accepted", reply.contains("loadaccepted"));
        
        // STEP 2: Invia doDeposit
        String depositMsg = CommUtils.buildDispatch(
            "tester", 
            "doDeposit", 
            "doDeposit(2)", 
            "cargomanager"
        ).toString();
        
        CommUtils.outblue("Step 2 - Sending doDeposit: " + depositMsg);
        connCargoManager.forward(depositMsg);
        
        // Attende completamento operazione
        Thread.sleep(2000);
        
        CommUtils.outgreen("[TEST 2] PASSED ✓");
    }
    
    /**
     * TEST 3: Verifica solo invio doDeposit
     */
    @Test
    public void testDepositOnly() throws Exception {
        CommUtils.outcyan("========================================");
        CommUtils.outcyan("TEST 3: Deposit Only");
        CommUtils.outcyan("========================================");
        
        // Invia solo doDeposit (senza loadrequest precedente)
        String depositMsg = CommUtils.buildDispatch(
            "tester", 
            "doDeposit", 
            "doDeposit(3)", 
            "cargomanager"
        ).toString();
        
        CommUtils.outblue("Sending doDeposit: " + depositMsg);
        connCargoManager.forward(depositMsg);
        
        // Attende elaborazione
        Thread.sleep(2000);
        
        CommUtils.outgreen("[TEST 3] PASSED ✓");
    }
    
    /**
     * TEST 4: Verifica multiple loadrequest in sequenza
     */
    @Test
    public void testMultipleLoadRequests() throws Exception {
        CommUtils.outcyan("========================================");
        CommUtils.outcyan("TEST 4: Multiple Load Requests");
        CommUtils.outcyan("========================================");
        
        for (int i = 1; i <= 3; i++) {
            String loadRequestMsg = CommUtils.buildRequest(
                "tester", 
                "loadrequest", 
                "loadrequest(" + i + ")", 
                "cargomanager"
            ).toString();
            
            CommUtils.outblue("Request #" + i + " - Sending: " + loadRequestMsg);
            String reply = connCargoManager.request(loadRequestMsg);
            CommUtils.outgreen("Received: " + reply);
            
            assertTrue("Request #" + i + " should be accepted", 
                reply.contains("loadaccepted"));
            
            Thread.sleep(500); // Pausa tra richieste
        }
        
        CommUtils.outgreen("[TEST 4] PASSED ✓");
    }
    
    /**
     * TEST 5: Verifica timeout su richiesta
     */
    @Test(timeout = 10000)
    public void testRequestTimeout() throws Exception {
        CommUtils.outcyan("========================================");
        CommUtils.outcyan("TEST 5: Request with Timeout");
        CommUtils.outcyan("========================================");
        
        String loadRequestMsg = CommUtils.buildRequest(
            "tester", 
            "loadrequest", 
            "loadrequest(99)", 
            "cargomanager"
        ).toString();
        
        CommUtils.outblue("Sending: " + loadRequestMsg);
        long startTime = System.currentTimeMillis();
        
        String reply = connCargoManager.request(loadRequestMsg);
        
        long elapsedTime = System.currentTimeMillis() - startTime;
        
        CommUtils.outgreen("Received reply in " + elapsedTime + "ms");
        assertNotNull("Should receive reply within timeout", reply);
        assertTrue("Should complete within reasonable time", elapsedTime < TIMEOUT);
        
        CommUtils.outgreen("[TEST 5] PASSED ✓");
    }
    
    /**
     * TEST 6: Verifica workflow completo con deposit multipli
     */
    @Test
    public void testMultipleDeposits() throws Exception {
        CommUtils.outcyan("========================================");
        CommUtils.outcyan("TEST 6: Multiple Deposits Workflow");
        CommUtils.outcyan("========================================");
        
        // Prima loadrequest
        String loadRequestMsg = CommUtils.buildRequest(
            "tester", 
            "loadrequest", 
            "loadrequest(10)", 
            "cargomanager"
        ).toString();
        
        String reply = connCargoManager.request(loadRequestMsg);
        assertTrue("First load should be accepted", reply.contains("loadaccepted"));
        
        // Multiple deposits
        for (int slot = 1; slot <= 3; slot++) {
            String depositMsg = CommUtils.buildDispatch(
                "tester", 
                "doDeposit", 
                "doDeposit(" + slot + ")", 
                "cargomanager"
            ).toString();
            
            CommUtils.outblue("Deposit #" + slot + " - Sending to slot " + slot);
            connCargoManager.forward(depositMsg);
            Thread.sleep(1500); // Attende completamento
        }
        
        CommUtils.outgreen("[TEST 6] PASSED ✓");
    }
    
    /**
     * Main method per eseguire tutti i test
     */
    public static void main(String[] args) {
        CommUtils.outcyan("\n╔════════════════════════════════════════╗");
        CommUtils.outcyan("║   CARGO SYSTEM - TEST INTERACTIONS    ║");
        CommUtils.outcyan("╚════════════════════════════════════════╝\n");
        
        try {
            testInteractions test = new testInteractions();
            
            // Esegui tutti i test
            test.setUp();
            
            try {
                test.testLoadRequestAccepted();
                Thread.sleep(500);
                
                test.testCompleteLoadWorkflow();
                Thread.sleep(500);
                
                test.testDepositOnly();
                Thread.sleep(500);
                
                test.testMultipleLoadRequests();
                Thread.sleep(500);
                
                test.testRequestTimeout();
                Thread.sleep(500);
                
                test.testMultipleDeposits();
                
                CommUtils.outgreen("\n╔════════════════════════════════════════╗");
                CommUtils.outgreen("║       ALL TESTS PASSED ✓✓✓            ║");
                CommUtils.outgreen("╚════════════════════════════════════════╝\n");
                
            } catch (Exception e) {
                CommUtils.outred("TEST FAILED: " + e.getMessage());
                e.printStackTrace();
            } finally {
                test.tearDown();
            }
            
        } catch (Exception e) {
            CommUtils.outred("SETUP FAILED: " + e.getMessage());
            e.printStackTrace();
        }
    }
}