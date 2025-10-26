%====================================================================================
% sprint0_system_overview description   
%====================================================================================
request( loadrequest, loadrerequest(pid,wheigth) ).
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
context(ctx_cargo, "localhost",  "TCP", "8014").
context(ctx_test, "localhost",  "TCP", "8015").
context(ctx_basicrobot, "127.0.0.1",  "TCP", "8020").
context(ctx_productservice, "127.0.0.1",  "TCP", "8111").
 qactor( cargoservice, ctx_cargo, "it.unibo.cargoservice.Cargoservice").
 static(cargoservice).
  qactor( productservice, ctx_productservice, "it.unibo.productservice.Productservice").
 static(productservice).
  qactor( led, ctx_sensor, "it.unibo.led.Led").
 static(led).
  qactor( sonar, ctx_sensor, "it.unibo.sonar.Sonar").
 static(sonar).
  qactor( cargorobot, ctx_cargo, "it.unibo.cargorobot.Cargorobot").
 static(cargorobot).
  qactor( basicrobot, ctx_basicrobot, "it.unibo.basicrobot.Basicrobot").
 static(basicrobot).
  qactor( mockactor, ctx_test, "it.unibo.mockactor.Mockactor").
 static(mockactor).
