import org.junit.Before;
import org.junit.After;
import org.junit.Test;

import TestSonarSystem.EventObserver;
import it.unibo.kactor.Protocol;

import org.junit.BeforeClass;
import static org.junit.Assert.*;

import unibo.basicomm23.utils.*;
import unibo.basicomm23.interfaces.IApplMessage;
import unibo.basicomm23.interfaces.Interaction;
import unibo.basicomm23.msg.ProtocolType;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/*
 * 
 * DA testare:
 * stopthesystem e resumeoperations
 * 
 */

public class TestInteractions {

	private static boolean systemStarted = false;
	private EventObserver observer;
	private Interaction sensorConnection;

	static class EventObserver {
		private boolean stopTheSys = false;
		private boolean resumeOp = false;
		private CountDownLatch stopLatch;
		private CountDownLatch resumeLatch;

		public EventObserver() {
			reset();
		}

		public void reset() {
			stopTheSys = false;
			resumeOp = false;
			stopLatch = new CountDownLatch(1);
			resumeLatch = new CountDownLatch(1);
		}

		public void onStopTheSystem() {
			stopTheSys = true;
			stopLatch.countDown();
			System.out.println("[OBSERVER] Evento STOPTHESYSTEM rilevato");
		}

		public void onResumeOperations() {
			resumeOp = true;
			resumeLatch.countDown();
			System.out.println("[OBSERVER] Evento RESUMEOPERATIONS rilevato");
		}

		public boolean hasReceivedStop() {
			return stopTheSys;
		}

		public boolean hasReceivedResume() {
			return resumeOp;
		}

		public boolean waitForStop(long timeoutSeconds) throws InterruptedException {
			return stopLatch.await(timeoutSeconds, TimeUnit.SECONDS);
		}

		public boolean waitForResume(long timeoutSeconds) throws InterruptedException {
			return resumeLatch.await(timeoutSeconds, TimeUnit.SECONDS);
		}
	}

	static class EventListenerThread extends Thread {
		private final EventObserver observer;
		private volatile boolean running = true;

		public EventListenerThread(EventObserver observer) {
			this.observer = observer;
		}

		@Override
        public void run() {
        	try {
        		// connessione su ctx_cargo
        		Interaction conn = ConnectionFactory.createClientSupport(
        				ProtocolType.TCP, "localhost", "8014"
        		);
        		while(running) {
        			IApplMessage msg = conn.receiveMsg();
        			
        			if(msg != null) {
        				String msgId = conn.msgId();
        				
        				if("stopthesystem".equals(msgId)) {
        					observer.onStopTheSystem();
        				} else if ("resumethesystem".equals(msgId)) {
        					observer.onResumeOperations();
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
                    it.unibo.ctx_cargo.main();
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
            "localhost", 8016, ProtocolType.tcp // corretta la connessione a ctx_sensor
        );
    }
    
    @After
    public void cleanup() throws InterruptedException {
        Thread.sleep(2000);
    }
    
    /*
     * TEST 1: Verifica stopthesystem e resumethesystem
     * */
    @Test
    public void testStopTheSystemWithObserver() throws Exception {
    	System.out.println("\n TEST 1: stopthesystem");
    	
    	observer.reset();
    	
    	// Reset del sistema
        System.out.println("Reset sistema...");
        sendMeasurements(new int[]{20, 20, 20});
        Thread.sleep(1500);
    	
    	// mando 3 misurazioni errate per scatenare sonaralert
        System.out.println("Invio 3 misurazioni errate...");
        sendMeasurements(new int[]{45, 40, 45});
        Thread.sleep(1500);
        
        boolean eventStopReceived = observer.waitForStop(5);
        assertTrue("stopthesystem riveuto", eventStopReceived);
        
        // Invia 1 misurazione valida
        System.out.println("\nInvio 1 misurazione valida...");
        observer.reset(); // Reset per nuovo ciclo
        sendMeasurements(new int[]{20});
        
        boolean resumeReceived = observer.waitForResume(5);
        assertTrue("resumethesystem ricevuto", resumeReceived);

        System.out.println("\n TEST SUPERATO");
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
