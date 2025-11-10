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
request( loadrequest, loadrequest(PID) ).
reply( loadaccepted, loadaccepted(X) ).  %%for loadrequest
reply( loadrejected, loadrejected(X) ).  %%for loadrequest
%====================================================================================
context(ctx_cargo, "127.0.0.1",  "TCP", "8014").
context(ctx_basicrobot, "127.0.0.1",  "TCP", "8020").
context(ctxproductservice, "127.0.0.1",  "TCP", "8111").
context(ctx_sensor, "localhost",  "TCP", "8016").
 qactor( productservice, ctxproductservice, "external").
  qactor( basicrobot, ctx_basicrobot, "external").
  qactor( cargoservice, ctx_cargo, "external").
  qactor( mockuser, ctx_sensor, "it.unibo.mockuser.Mockuser").
 static(mockuser).
