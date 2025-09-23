%====================================================================================
% sprint0_system_overview description   
%====================================================================================
request( loadrequest, loadrerequest(pid,wheigth) ).
dispatch( stop, stop(X) ).
dispatch( resume, resume(X) ).
request( start_load, start_load(pid) ).
dispatch( reply_success, reply_success(N) ).
%====================================================================================
context(ctx_cargo, "localhost",  "TCP", "8014").
context(ctx_gui, "localhost",  "TCP", "8015").
context(ctx_sensor, "localhost",  "TCP", "8016").
context(ctx_cargorobot, "localhost",  "TCP", "8017").
context(ctx_test, "localhost",  "TCP", "8018").
 qactor( cargomanager, ctx_cargo, "it.unibo.cargomanager.Cargomanager").
 static(cargomanager).
  qactor( productservice, ctx_cargo, "it.unibo.productservice.Productservice").
 static(productservice).
  qactor( led, ctx_sensor, "it.unibo.led.Led").
 static(led).
  qactor( sonar, ctx_sensor, "it.unibo.sonar.Sonar").
 static(sonar).
  qactor( cargorobot, ctx_cargorobot, "it.unibo.cargorobot.Cargorobot").
 static(cargorobot).
  qactor( mockactor, ctx_test, "it.unibo.mockactor.Mockactor").
 static(mockactor).
