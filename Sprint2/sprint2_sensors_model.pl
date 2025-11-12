%====================================================================================
% sprint2_sensors_model description   
%====================================================================================
event( doDeposit, doDeposit(X) ).
event( waitingForDeposit, waitingfordeposit(X) ).
event( stopWaitingForDeposit, stopWaitingForDeposit(X) ).
event( productloaded, productloaded(X) ).
event( alarm, alarm(ARG) ).
event( sonaralert, sonaralert(X) ).
event( sonarok, sonarok(X) ).
event( stopthesystem, stopthesystem(X) ).
event( resumethesystem, resumethesystem(X) ).
event( measurement, measurement(CM) ).
request( loadrequest, loadrequest(PID) ).
reply( loadaccepted, loadaccepted(X) ).  %%for loadrequest
reply( loadrejected, loadrejected(X) ).  %%for loadrequest
%====================================================================================
context(ctx_sensor, "localhost",  "TCP", "8016").
 qactor( sonar, ctx_sensor, "it.unibo.sonar.Sonar").
 static(sonar).
  qactor( sonarmanager, ctx_sensor, "it.unibo.sonarmanager.Sonarmanager").
 static(sonarmanager).
  qactor( led, ctx_sensor, "it.unibo.led.Led").
 static(led).
