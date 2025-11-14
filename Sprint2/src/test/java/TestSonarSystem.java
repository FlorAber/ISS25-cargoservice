import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import org.junit.BeforeClass;
import static org.junit.Assert.*;

import unibo.basicomm23.utils.*;
import unibo.basicomm23.interfaces.IApplMessage;
import unibo.basicomm23.interfaces.Interaction;
import unibo.basicomm23.msg.ProtocolType;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Test JUnit per il sistema Sonar - VERSIONE CON OBSERVER
 * Utilizza un pattern Observer per intercettare gli eventi
 * sonaralert e sonarok invece di analizzare i log
 * 
 * APPROCCIO: Crea un attore observer che si sottoscrive agli eventi
 * e verifica le transizioni di stato in modo programmatico
 */
public class TestSonarSystem {
    
    private static boolean systemStarted = false;
    private EventObserver observer;
    private Interaction sensorConnection;
    
    /**
     * Observer che cattura gli eventi sonaralert e sonarok
     */
    static class EventObserver {
        private boolean ledOn = false;
        private boolean alertReceived = false;
        private boolean okReceived = false;
        private boolean doDepositReceived = false;
        private CountDownLatch alertLatch;
        private CountDownLatch okLatch;
        private CountDownLatch doDepositLatch;
        public EventObserver() {
            reset();
        }
        
        public void reset() {
            ledOn = false;
            alertReceived = false;
            okReceived = false;
            doDepositReceived = false;
            alertLatch = new CountDownLatch(1);
            okLatch = new CountDownLatch(1);
            doDepositLatch = new CountDownLatch(1);
        }
        
        public void onSonarAlert() {
            ledOn = true;
            alertReceived = true;
            alertLatch.countDown();
            System.out.println("[OBSERVER] Evento SONARALERT ricevuto - LED ACCESO");
        }
        
        public void onSonarOk() {
            ledOn = false;
            okReceived = true;
            okLatch.countDown();
            System.out.println("[OBSERVER] Evento SONAROK ricevuto - LED SPENTO");
        }
        
        public void onDoDeposit() {
        	doDepositReceived = true;
        	doDepositLatch.countDown();
            System.out.println("[OBSERVER] Evento DODEPOSIT ricevuto");
        }
        
        public boolean isLedOn() {
            return ledOn;
        }
        
        public boolean hasReceivedAlert() {
            return alertReceived;
        }
        
        public boolean hasReceivedOk() {
            return okReceived;
        }
        
        public boolean hasReceivedDoDeposit() {
        	return doDepositReceived;
        }
        
        public boolean waitForAlert(long timeoutSeconds) throws InterruptedException {
            return alertLatch.await(timeoutSeconds, TimeUnit.SECONDS);
        }
        
        public boolean waitForOk(long timeoutSeconds) throws InterruptedException {
            return okLatch.await(timeoutSeconds, TimeUnit.SECONDS);
        }
        
        public boolean waitForDoDeposit(long timeoutSeconds) throws InterruptedException {
        	return doDepositLatch.await(timeoutSeconds, TimeUnit.SECONDS);
        }
    }
    
    /**
     * Thread che ascolta gli eventi dal sistema
     */
    static class EventListenerThread extends Thread {
        private final EventObserver observer;
        private volatile boolean running = true;
        
        public EventListenerThread(EventObserver observer) {
            this.observer = observer;
        }
        
        @Override
        public void run() {
            try {
                // Connessione per ricevere eventi
                Interaction conn = ConnectionFactory.createClientSupport(
                	 ProtocolType.tcp,"localhost", "8016"
                );
                
                while (running) {
                    // Attendi messaggi
                    IApplMessage msg = conn.receiveMsg();
                    
                    if (msg != null) {
                        String msgId = msg.msgId();
                        
                        if ("sonaralert".equals(msgId)) {
                            observer.onSonarAlert();
                        } else if ("sonarok".equals(msgId)) {
                            observer.onSonarOk();
                        } else if ("doDeposit".equals(msgId)) {
                        	observer.onDoDeposit();
                        }
                    }
                    
                    Thread.sleep(100);
                }
            } catch (Exception e) {
                System.err.println("Errore nel listener: " + e.getMessage());
            }
        }
        
        public void stopListening() {
            running = false;
        }
    }
    
    @BeforeClass
    public static void setupClass() throws InterruptedException {
        // Avvia il sistema una sola volta
        if (!systemStarted) {
            new Thread(() -> {
                try {
                    it.unibo.ctx_sensor.main();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
            
            systemStarted = true;
            Thread.sleep(6000);
        }
    }
    
    @Before
    public void setup() throws Exception {
        System.out.println("\n========================================");
        System.out.println("Setup del test con Observer");
        System.out.println("========================================");
        
        // Crea observer e avvia listener
        observer = new EventObserver();
        
        // Nota: In una implementazione reale, dovremmo creare un QActor
        // che si sottoscrive agli eventi. Qui usiamo un approccio semplificato.
        
        // Connessione al context sensor
        sensorConnection = CommUtils.connectWithContext(
            "localhost", 8016, ProtocolType.tcp
        );
    }
    
    @After
    public void cleanup() throws InterruptedException {
        Thread.sleep(2000);
    }
    
    /**
     * TEST 1: Verifica errore dopo 3 misurazioni guaste con Observer
     */
    @Test
    public void testErrorStateWithObserver() throws Exception {
        System.out.println("\n===== TEST 1 (Observer): Errore dopo 3 misurazioni =====");
        
        observer.reset();
        
        // Reset sistema
        System.out.println("Reset sistema...");
        sendMeasurements(new int[]{20, 20, 20});
        Thread.sleep(1500);
        
        // Verifica stato iniziale
        assertFalse("LED deve essere spento inizialmente", observer.isLedOn());
        System.out.println("✓ Stato iniziale: LED SPENTO");
        
        // Invia 3 misurazioni con guasto
        System.out.println("\nInvio 3 misurazioni guaste...");
        sendMeasurements(new int[]{40, 45, 50});
        
        // Attendi evento sonaralert
        boolean alertReceived = observer.waitForAlert(5);
        assertTrue("Deve ricevere evento sonaralert", alertReceived);
        assertTrue("LED deve essere acceso", observer.isLedOn());
        System.out.println("✓ ERRORE rilevato - LED ACCESO");
        
        // Invia 1 misurazione valida
        System.out.println("\nInvio 1 misurazione valida...");
        observer.reset(); // Reset per nuovo ciclo
        sendMeasurements(new int[]{20});
        
        // Attendi evento sonarok
        boolean okReceived = observer.waitForOk(5);
        assertTrue("Deve ricevere evento sonarok", okReceived);
        assertFalse("LED deve essere spento", observer.isLedOn());
        System.out.println("✓ RIENTRO confermato - LED SPENTO");
        
        System.out.println("\n===== TEST 1 PASSED ✓ =====\n");
    }
    
    /**
     * TEST 2: Verifica ricezione doDeposit
     */
    @Test
    public void testLedTransitionsWithObserver() throws Exception {
        System.out.println("\n===== TEST 2 (Observer): VERIFICA doDeposit =====");
        
        observer.reset();
        
        // Reset
        sendMeasurements(new int[]{20, 20, 20});
        Thread.sleep(1500);
        
        // Stato iniziale
        assertFalse("LED inizialmente spento", observer.isLedOn());
        System.out.println("✓ LED iniziale: SPENTO");
        
        // Genera pacco presente
        System.out.println("\nGenerazione pacco presente...");
        sendMeasurements(new int[]{20, 20, 20});
        observer.waitForAlert(5);
        
        // doDeposit check
        boolean doDepositReceive = observer.waitForDoDeposit(3);
        assertTrue("doDeposit ricevuto", observer.hasReceivedDoDeposit());
        
        System.out.println("\n===== TEST 2 PASSED ✓ =====\n");
    }
    
    /**
     * TEST 3: Verifica nessun errore con 2 misurazioni
     */
    @Test
    public void testNoErrorWithTwoFaultyMeasurements() throws Exception {
        System.out.println("\n===== TEST 3 (Observer): Solo 2 misurazioni guaste =====");
        
        observer.reset();
        
        // Reset
        sendMeasurements(new int[]{20, 20, 20});
        Thread.sleep(1500);
        
        // Invia solo 2 misurazioni guaste
        System.out.println("Invio 2 misurazioni guaste...");
        sendMeasurements(new int[]{40, 45});
        
        // Attendi (NON deve arrivare alert)
        boolean alertReceived = observer.waitForAlert(3);
        assertFalse("NON deve ricevere sonaralert", alertReceived);
        assertFalse("LED deve rimanere spento", observer.isLedOn());
        System.out.println("✓ Sistema correttamente NON in errore");
        
        System.out.println("\n===== TEST 3 PASSED ✓ =====\n");
    }
    
    /**
     * TEST 4: Test di stress - cicli multipli
     */
    @Test
    public void testMultipleCycles() throws Exception {
        System.out.println("\n===== TEST 4 (Observer): Cicli multipli =====");
        
        for (int cycle = 1; cycle <= 2; cycle++) {
            System.out.println("\n--- Ciclo " + cycle + " ---");
            observer.reset();
            
            // Errore
            sendMeasurements(new int[]{50, 50, 50});
            assertTrue("Ciclo " + cycle + ": deve ricevere alert", 
                observer.waitForAlert(5));
            System.out.println("✓ Errore generato");
            
            // Rientro
            observer.reset();
            sendMeasurements(new int[]{20});
            assertTrue("Ciclo " + cycle + ": deve ricevere ok", 
                observer.waitForOk(5));
            System.out.println("✓ Rientro confermato");
        }
        
        System.out.println("\n===== TEST 4 PASSED ✓ =====\n");
    }
    
    // ========== UTILITY METHODS ==========
    
    private void sendMeasurements(int[] values) throws Exception {
        for (int value : values) {
            IApplMessage msg = CommUtils.buildEvent(
                "tester",
                "measurement",
                "measurement(" + value + ")"
            );
            sensorConnection.forward(msg);
            Thread.sleep(1100);
        }
    }
}