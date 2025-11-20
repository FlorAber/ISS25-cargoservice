let ws = null;
let robotMoving = false;
let robotPosition = 30;
let robotDirection = 1;
let animationFrame = null;

// Create slots
function createSlots() {
  const container = document.getElementById("slotsContainer");
  container.innerHTML = "";

  for (let i = 1; i <= 4; i++) {
    const slot = document.createElement("div");
    slot.className = "slot empty";
    slot.id = `slot${i}`;
    slot.innerHTML = `
                    <div class="slot-header">SLOT ${i}</div>
                    <div class="slot-field"><strong>PID:</strong> <span id="pid${i}">-</span></div>
                    <div class="slot-field"><strong>nome:</strong> <span id="name${i}">-</span></div>
                    <div class="slot-field"><strong>peso:</strong> <span id="weight${i}">0</span> kg</div>
                `;
    container.appendChild(slot);
  }
}

// Update UI with WebSocket data
function updateUI(data) {
  let totalWeight = 0;
  let occupiedSlots = 0;

  for (let i = 0; i < 4; i++) {
    const slotElement = document.getElementById(`slot${i + 1}`);
    const pid = data.pids[i];
    const name = data.names[i];
    const weight = data.weights[i];

    // Update slot data
    document.getElementById(`pid${i + 1}`).textContent = pid || "-";
    document.getElementById(`name${i + 1}`).textContent = name || "-";
    document.getElementById(`weight${i + 1}`).textContent = weight || 0;

    // Update slot status
    if (pid && pid !== 0 && pid !== "") {
      slotElement.className = "slot occupied";
      occupiedSlots++;
      totalWeight += weight || 0;
    } else {
      slotElement.className = "slot empty";
    }
  }

  // Update weight info
  document.getElementById("currentWeight").textContent = totalWeight;
  document.getElementById("maxWeight").textContent = data.MAXLOAD || 0;

  // Update status indicator
  const statusIndicator = document.getElementById("statusIndicator");
  if (occupiedSlots === 4) {
    statusIndicator.textContent = "Sfondo Giallo - Magazzino Pieno";
    statusIndicator.className = "status-indicator full";
  } else {
    statusIndicator.textContent = "Sfondo Verde - Magazzino Libero";
    statusIndicator.className = "status-indicator empty";
  }
}

// Robot animation
function moveRobot() {
  if (!robotMoving) return;

  const robot = document.querySelector(".robot");
  const robotImg = document.getElementById("robotImg");
  const maxPosition = 1200;

  robotPosition += robotDirection * 2;

  if (robotPosition >= maxPosition) {
    robotDirection = -1;
    robotImg.style.transform = "scaleX(-1)"; // Specchia l'immagine
  } else if (robotPosition <= 30) {
    robotDirection = 1;
    robotImg.style.transform = "scaleX(1)"; // Immagine normale
  }

  robot.style.left = robotPosition + "px";
  animationFrame = requestAnimationFrame(moveRobot);
}

function startRobot() {
  if (!robotMoving) {
    robotMoving = true;
    moveRobot();
  }
}

function stopRobot() {
  robotMoving = false;
  if (animationFrame) {
    cancelAnimationFrame(animationFrame);
  }
}

// WebSocket connection
function connectWebSocket() {
  // Replace with your WebSocket URL
  const wsUrl = "ws://localhost:8080"; // Modify this with your actual WebSocket server

  try {
    ws = new WebSocket(wsUrl);

    ws.onopen = function () {
      console.log("WebSocket connected");
      const statusEl = document.getElementById("connectionStatus");
      statusEl.textContent = "✅ Connesso al server";
      statusEl.className = "connection-status connected";
    };

    ws.onmessage = function (event) {
      try {
        const data = JSON.parse(event.data);
        updateUI(data);
      } catch (e) {
        console.error("Error parsing JSON:", e);
      }
    };

    ws.onerror = function (error) {
      console.error("WebSocket error:", error);
      const statusEl = document.getElementById("connectionStatus");
      statusEl.textContent = "❌ Errore di connessione";
      statusEl.className = "connection-status disconnected";
    };

    ws.onclose = function () {
      console.log("WebSocket disconnected");
      const statusEl = document.getElementById("connectionStatus");
      statusEl.textContent = "⚠️ Disconnesso - Tentativo di riconnessione...";
      statusEl.className = "connection-status disconnected";

      // Try to reconnect after 3 seconds
      setTimeout(connectWebSocket, 3000);
    };
  } catch (e) {
    console.error("Failed to create WebSocket:", e);
  }
}

// Send request button
document
  .getElementById("sendRequestBtn")
  .addEventListener("click", function () {
    const idenValue = document.getElementById("idenInput").value;

    if (!idenValue || idenValue === "") {
      alert("Inserisci un numero identificativo!");
      return;
    }

    if (ws && ws.readyState === WebSocket.OPEN) {
      // Send the identification number via WebSocket
      const request = {
        action: "request",
        iden: parseInt(idenValue),
        timestamp: Date.now(),
      };
      ws.send(JSON.stringify(request));
      console.log("Richiesta inviata:", request);
      alert("Richiesta inviata con ID: " + idenValue);
    } else {
      alert("WebSocket non connesso!");
    }
  });

// Initialize
createSlots();

// Try to connect to WebSocket
connectWebSocket();

// Export functions for external control
window.startRobotMovement = startRobot;
window.stopRobotMovement = stopRobot;

document.addEventListener("DOMContentLoaded", function () {
  // ... tutto il codice ...

  // Avvia il robot in demo dopo 2 secondi
  setTimeout(function () {
    if (!ws || ws.readyState !== WebSocket.OPEN) {
      console.log("Starting robot animation");
      startRobot(); // Avvia l'animazione
    }
  }, 2000);
});
