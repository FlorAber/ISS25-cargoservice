%====================================================================================
% sprint2_sensor description   
%====================================================================================
event( doDeposit, doDeposit(X) ).
event( waitingForDeposit, waitingfordeposit(X) ).
event( stopWaitingForDeposit, stopWaitingForDeposit(X) ).
event( sonaralert, sonaralert(X) ).
event( sonarok, sonarok(X) ).
dispatch( measurement, measurement(CM) ).
%====================================================================================
context(ctx_cargo, "172.20.10.10",  "TCP", "8014").
context(ctx_sensor, "localhost",  "TCP", "8016").
 qactor( sonar, ctx_sensor, "it.unibo.sonar.Sonar").
 static(sonar).
  qactor( sonarmanager, ctx_sensor, "it.unibo.sonarmanager.Sonarmanager").
 static(sonarmanager).
  qactor( led, ctx_sensor, "it.unibo.led.Led").
 static(led).
