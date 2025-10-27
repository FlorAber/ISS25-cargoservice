%====================================================================================
% sprint0_system_overview description   
%====================================================================================
request( loadrequest, loadrerequest(pid,wheigth) ).
reply( loadaccepted, loadaccepted(X) ).  %%for loadrequest
reply( loadrejected, loadrejected(X) ).  %%for loadrequest
dispatch( stop, stop(X) ).
dispatch( resume, resume(X) ).
request( start_load, start_load(pid) ).
dispatch( reply_success, reply_success(N) ).
request( getProduct, product(ID) ).
%====================================================================================
context(ctx_cargo, "localhost",  "TCP", "8014").
context(ctx_gui, "localhost",  "TCP", "8015").
context(ctx_sensor, "localhost",  "TCP", "8016").
context(ctx_test, "localhost",  "TCP", "8018").
context(ctx_basicrobot, "127.0.0.1",  "TCP", "8020").
context(ctx_productservice, "127.0.0.1",  "TCP", "8111").
 qactor( productservice, ctx_productservice, "external").
  qactor( basicrobot, ctx_basicrobot, "external").
  qactor( cargoservice, ctx_cargo, "it.unibo.cargoservice.Cargoservice").
 static(cargoservice).
  qactor( led, ctx_sensor, "it.unibo.led.Led").
 static(led).
  qactor( sonar, ctx_sensor, "it.unibo.sonar.Sonar").
 static(sonar).
  qactor( cargorobot, ctx_cargo, "it.unibo.cargorobot.Cargorobot").
 static(cargorobot).
  qactor( mockuser, ctx_test, "it.unibo.mockuser.Mockuser").
 static(mockuser).
