/*
wsminimal.js
*/

var socket;

function sendMessage(message) {
  var jsonMsg = JSON.stringify({ name: message });
  socket.send(jsonMsg);
  console.log("Sent Message: " + jsonMsg);
}

function connect() {
  var protocol = window.location.protocol === "https:" ? "wss://" : "ws://";
  var host = document.location.host;
  var pathname = "/holdupdates";

  var addr = protocol + host + pathname;

  console.log(addr);
  // Assicura che sia aperta un unica connessione
  if (socket !== undefined && socket.readyState !== WebSocket.CLOSED) {
    alert("WARNING: Connessione WebSocket già stabilita");
  }
  socket = new WebSocket(addr);

  socket.onopen = function (event) {
    console.log("Connected to " + addr);
  };

  //Sulla WS scrive WSHandler
  socket.onmessage = function (event) {
    msg = event.data;
    alert(`Got Message: ${msg}`);
    //console.log("ws-status:" + msg);
    // if (msg.includes("plan"))
    //   addMessageToWindow(planexecDisplay, msg); //in ioutils
    // //else if( msg.includes("RobotPos") ) setMessageToWindow(robotDisplay,msg);
    // //setMessageToWindow(robotDisplay,msg); //""+`${event.data}`*/
    // else addToMessageArea(msg);
  };
} //connect

connect();
